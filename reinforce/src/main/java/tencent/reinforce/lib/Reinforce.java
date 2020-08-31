package tencent.reinforce.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ms.v20180408.MsClient;
import com.tencentcloudapi.ms.v20180408.models.AppInfo;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceRequest;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceResponse;
import com.tencentcloudapi.ms.v20180408.models.DescribeResourceInstancesRequest;
import com.tencentcloudapi.ms.v20180408.models.DescribeResourceInstancesResponse;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldPlanInstanceRequest;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldPlanInstanceResponse;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultRequest;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultResponse;
import com.tencentcloudapi.ms.v20180408.models.PlanDetailInfo;
import com.tencentcloudapi.ms.v20180408.models.ResourceInfo;
import com.tencentcloudapi.ms.v20180408.models.ServiceInfo;
import com.tencentcloudapi.ms.v20180408.models.ShieldPlanInfo;

import org.gradle.api.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import bean.Code;
import bean.EnumToString;
import bean.Param;
import bean.ReturnCode;
import bean.ShieldStrategy;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import util.CommonUtil;

/**
 * On 2020-05-19
 */
public class Reinforce {

    private static AppInfo appInfo = new AppInfo();
    private static ServiceInfo serviceInfo = new ServiceInfo();
    private Param param;
    private String uploadApkName = "";
    private String originalApkPath = "";
    private static final String version = "1.0.3";
    private String resourceId = "";
    private int pid = 0;
    private int planId = 0;
    private String itemid = "";
    private DescribeShieldResultResponse response;

    private Logger mLogger;
    private OkHttpClient mOkHttpClient;
    private ClientProfile mProfile;
    private Gson mGson;

    private Reinforce(Builder builder) {
        param = builder.param;
        mLogger = builder.logger;

        mOkHttpClient = new OkHttpClient();
        mOkHttpClient.setConnectTimeout(360, TimeUnit.SECONDS);
        mOkHttpClient.setWriteTimeout(360, TimeUnit.SECONDS);
        mOkHttpClient.setReadTimeout(360, TimeUnit.SECONDS);

        mProfile = new ClientProfile();
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setConnTimeout(360);
        httpProfile.setReadTimeout(360);
        httpProfile.setWriteTimeout(360);
        mProfile.setHttpProfile(httpProfile);

        mGson = new Gson();
    }

    public int start() {
        mLogger.warn("uploading apk");

        originalApkPath = param.getUploadPath();
        File apk = new File(originalApkPath);
        uploadApkName = CommonUtil.getBaseName(apk.getName());
        TreeMap<String, Object> treeMap = CommonUtil.prepareFile(param);

        String serverURL = "https://ms.cloud.tencent.com/data/upload";

        try {
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("file", apk.getName(),
                            ProgressRequestBody.create(MediaType.parse("application/vnd.android.package-archive"), apk));
            Iterator<Map.Entry<String, Object>> iterator = treeMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                builder.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(serverURL).post(requestBody).build();
            Call call = mOkHttpClient.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                String string = response.body().string();
                Code code = mGson.fromJson(string, Code.class);
                if (code.getCode() == 0) {
                    mLogger.warn("upload success");
                    param.setPkg_name(code.getData().getAppInfo().getAppPkgName());
                    param.setUploadPath(code.getData().getAppInfo().getAppUrl());
                    param.setMd5(code.getData().getAppInfo().getAppMd5());
                    param.setAppIconUrl(code.getData().getAppInfo().getAppIconUrl());
                    param.setAppSize(code.getData().getAppInfo().getAppSize());
                    param.setVersion(code.getData().getAppInfo().getAppVersion());
                    param.setFileName(code.getData().getAppInfo().getFileName());
                    param.setAppName(code.getData().getAppInfo().getAppName());
                    getResource();
                    prepareApkFile();
                    return 0;
                } else {
                    mLogger.warn(prettyJson(mGson.toJson(new EnumToString(code.getCode(), code.getMsg()))));
                    return 1;
                }
            } else {
                mLogger.warn("upload failed code: " + response.code());
                return 1;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            handleError(e.getMessage());
            return 1;
        }

    }

    private void prepareApkFile() throws Exception {
        appInfo.setAppUrl(param.getUploadPath());
        appInfo.setAppMd5(param.getMd5());
        appInfo.setAppPkgName(param.getPkg_name());
        appInfo.setAppIconUrl(param.getAppIconUrl());
        appInfo.setAppSize(param.getAppSize());
        appInfo.setAppVersion(param.getAppVersion());
        appInfo.setFileName(param.getFileName());
        appInfo.setAppName(param.getAppName());
        serviceInfo.setCallbackUrl("");
        if (planId != 0) {
            serviceInfo.setPlanId(planId);
        }

        serviceInfo.setSubmitSource(CommonUtil.getSource(param, originalApkPath));
        startShield(param);
    }


    private void startShield(Param param) throws Exception {
        mLogger.warn("request reinforce");

        Credential credential = new Credential(param.getSid(), param.getSkey());
        MsClient client = new MsClient(credential, "", mProfile);
        CreateShieldInstanceRequest request = new CreateShieldInstanceRequest();
        request.setAppInfo(appInfo);
        request.setServiceInfo(serviceInfo);
        CreateShieldInstanceResponse resp = client.CreateShieldInstance(request);
        checkStatus(resp.getProgress(), "create", itemid = resp.getItemId());

    }

    private void checkStatus(int status, String from, String itemId) throws Exception {
        switch (status) {
            case 1:
                if (from.equals("result")) {
                    String md5 = response.getShieldInfo().getShieldMd5();
                    String url = response.getShieldInfo().getAppUrl();
                    mLogger.warn("reinforce success, downloading");
                    download(md5, url, getDownFilePath(), "download reinforced apk success");
                }
                break;
            case 2:
                if (from.equals("create")) {
                    mLogger.warn("request success");
                    checkResult(itemId, param);
                } else if (from.equals("result")) {
                    try {
                        TimeUnit.SECONDS.sleep(5L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        throw new Exception("Interrupt");
                    }

                    checkResult(itemId, param);
                }
                break;
            case 3:
                if (from.equals("result")) {
                    int code = response.getShieldInfo().getShieldCode();
                    throw new Exception("error code: " + code);
                } else {
                    throw new Exception(prettyJson(CommonUtil.adapter(ReturnCode.SHIELDERROR)));
                }
            case 4:
                throw new Exception(prettyJson(CommonUtil.adapter(ReturnCode.SHIELDERROR)));
            default:
                break;
        }

    }

    private static String prettyJson(String resp) {
        Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(resp);
        return gson.toJson(je);
    }

    private void download(final String md5, String url, final String fileName, final String msg) throws Exception {
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();
        if (response.isSuccessful()) {
            ResponseBody body = response.body();
            try {
                int mLastPercent = -1;
                long contentLength = body.contentLength();
                BufferedSource source = body.source();
                File down = new File(fileName);
                if (down.exists()) {
                    down.delete();
                }

                mLogger.warn("download apk path: " + down);
                BufferedSink sink = Okio.buffer(Okio.sink(down));
                Buffer sinkBuffer = sink.buffer();
                long totalBytesRead = 0L;

                long bytesRead;
                for (short bufferSize = 8192; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
                    sink.emit();
                    totalBytesRead += bytesRead;
                    if (contentLength > 0) {
                        int percent = (int) (totalBytesRead * 1f / contentLength * 100);
                        if (mLastPercent != percent) {
                            mLastPercent = percent;
                            System.out.println("Downloaded " + percent + "%");
                        }
                    }

                }
                System.out.println();

                sink.flush();
                sink.close();
                source.close();
                if (msg.equals("download reinforced apk success")) {
                    mLogger.warn("reinforced apk MD5: " + md5);
                }

                if (md5.toLowerCase().equals(CommonUtil.getFileMd5(down))) {
                    success(msg);
                } else {
                    throw new Exception(prettyJson(adapter(ReturnCode.SIGNATURE)));
                }
            } catch (IOException var15) {
                throw new Exception(prettyJson(adapter(ReturnCode.DOWNLOADPATH)));
            }
        } else if (response.code() > 400 && response.code() < 500) {
            throw new Exception(prettyJson(adapter(ReturnCode.CLIENTERROR)));
        } else {
            throw new Exception(prettyJson(adapter(ReturnCode.SERVERERROR)));
        }

    }

    private void success(String msg) {
        mLogger.warn(msg);
        mLogger.warn("reinforced apk need resign");
        mLogger.warn("reinforce and resigned apk must be test");
    }

    public String getDownFilePath() {
        String basename = "";
        if ((new File(param.getDownPath())).isDirectory()) {
            basename = param.getDownPath() + File.separator;
        }

        if (param.getUploadType().equals("file")) {
            basename = basename + uploadApkName + "-reinforced.apk";
        } else if (param.getUploadType().equals("url")) {
            basename = basename + "my_legu.apk";
        }

        return basename;
    }

    private void checkResult(String requestId, Param param) throws Exception {
        mLogger.warn("reinforcing apk.......");
        Credential credential = new Credential(param.getSid(), param.getSkey());
        MsClient client = new MsClient(credential, "", mProfile);
        DescribeShieldResultRequest resultRequest = new DescribeShieldResultRequest();
        resultRequest.setItemId(requestId);

        response = client.DescribeShieldResult(resultRequest);
        checkStatus(response.getTaskStatus(), "result", requestId);

    }

    private void getResource() throws Exception {
        Credential credential = new Credential(param.getSid(), param.getSkey());
        MsClient client = new MsClient(credential, "", mProfile);
        DescribeResourceInstancesRequest request = new DescribeResourceInstancesRequest();
        request.setPids(new Integer[]{12750, 13624, 12767, 12766, 12736});
        request.setLimit(100);
        request.setOrderDirection("desc");
        DescribeResourceInstancesResponse resp = client.DescribeResourceInstances(request);
        int totalCount = resp.getTotalCount();
        ResourceInfo[] resourceInfos = resp.getResourceSet();
        if (totalCount != 0) {
            String pkg_name = "";
            boolean flag = false;
            int var10 = resourceInfos.length;

            for (int i = 0; i < var10; ++i) {
                ResourceInfo info = resourceInfos[i];
                pid = info.getPid();
                resourceId = info.getResourceId();
                pkg_name = info.getBindInfo().getAppPkgName();
                if (pid == 12750 && pkg_name.equals(param.getPkg_name())) {
                    serviceInfo.setServiceEdition("enterprise");
                    mLogger.warn("Enterprise Level Reinforce");
                    flag = true;
                    break;
                }

                if (pid == 13624 && pkg_name.equals(param.getPkg_name())) {
                    serviceInfo.setServiceEdition("professional");
                    mLogger.warn("Professional Level Reinforce");
                    flag = true;
                    break;
                }
            }

            if (flag) {
                getShieldPlan();
            } else {
                serviceInfo.setServiceEdition("basic");
                mLogger.warn("Basic Level Reinforce");
            }
        } else {
            serviceInfo.setServiceEdition("basic");
            mLogger.warn("Basic Level Reinforce");
        }

    }

    private void getShieldPlan() throws Exception {
        Credential credential = new Credential(param.getSid(), param.getSkey());
        MsClient client = new MsClient(credential, "", mProfile);
        DescribeShieldPlanInstanceRequest request = new DescribeShieldPlanInstanceRequest();
        request.setPid(pid);
        request.setResourceId(resourceId);
        DescribeShieldPlanInstanceResponse resp = client.DescribeShieldPlanInstance(request);
        new ShieldStrategy(resp.getBindInfo(), resp.getShieldPlanInfo(), resp.getResourceServiceInfo(), resp.getRequestId());
        ShieldPlanInfo shieldPlanInfo = resp.getShieldPlanInfo();
        if (shieldPlanInfo.getTotalCount() != 0) {
            PlanDetailInfo[] infos = shieldPlanInfo.getPlanSet();
            int var9 = infos.length;

            for (int i = 0; i < var9; ++i) {
                PlanDetailInfo info = infos[i];
                if (info.getIsDefault() == 1) {
                    planId = info.getPlanId();
                    break;
                }
            }
        }
    }

    private void handleError(String message) {
        if (message.toLowerCase().contains("AuthFailure".toLowerCase())) {
            mLogger.warn(prettyJson(adapter(ReturnCode.AUTHFAILURE)));
        } else if (message.toLowerCase().contains("timed out".toLowerCase())) {
            mLogger.warn(prettyJson(adapter(ReturnCode.TIMEOUT)));
        } else {
            mLogger.warn(message);
        }
    }

    private String adapter(Object obj) {
        EnumToString string = new EnumToString(obj);
        return mGson.toJson(string);
    }


    public static class Builder {
        private Param param = new Param();
        private Logger logger;

        public Builder(Logger logger) {
            this.logger = logger;
            param.setUploadType("file");
            param.setDownloadType("file");
        }

        public Builder setSid(String sid) {
            param.setSid(sid);
            return this;
        }

        public Builder setSkey(String skey) {
            param.setSkey(skey);
            return this;
        }

        public Builder setUploadPath(String apkPath) {
            param.setUploadPath(apkPath);
            return this;
        }

        public Builder setDownloadPath(String apkPath) {
            param.setDownPath(apkPath);
            return this;
        }

        public Reinforce build() {
            return new Reinforce(this);
        }
    }
}
