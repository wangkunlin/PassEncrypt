package tencent.reinforce.lib;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
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
import util.ErrorUtil;

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

    private Reinforce(Builder builder) {
        param = builder.param;
        mLogger = builder.logger;
    }

    public void start() {
        mLogger.warn("uploading apk");

        originalApkPath = param.getUploadPath();
        File apk = new File(originalApkPath);
        uploadApkName = CommonUtil.getBaseName(apk.getName());
        TreeMap<String, Object> treeMap = CommonUtil.prepareFile(param);

        String serverURL = "https://ms.cloud.tencent.com/data/upload";

        try {
            MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM)
                    .addFormDataPart("file", apk.getName(),
                            RequestBody.create(MediaType.parse("application/vnd.android.package-archive"), apk));
            Iterator<Map.Entry<String, Object>> iterator = treeMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                builder.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
            }

            RequestBody requestBody = builder.build();
            Request request = new Request.Builder().url(serverURL).post(requestBody).build();
            OkHttpClient client = new OkHttpClient();
            Call call = client.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                String string = response.body().string();
                Code code = new Gson().fromJson(string, Code.class);
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
                } else {
                    CommonUtil.prettyJson((new Gson()).toJson(new EnumToString(code.getCode(), code.getMsg())));
                }
            } else {
                mLogger.warn("upload failed code: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void prepareApkFile() {
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


    private void startShield(Param param) {
        mLogger.warn("request reinforce");

        try {
            Credential credential = new Credential(param.getSid(), param.getSkey());
            MsClient client = new MsClient(credential, "");
            CreateShieldInstanceRequest request = new CreateShieldInstanceRequest();
            request.setAppInfo(appInfo);
            request.setServiceInfo(serviceInfo);
            CreateShieldInstanceResponse resp = client.CreateShieldInstance(request);
            checkStatus(resp.getProgress(), "create", itemid = resp.getItemId());
        } catch (TencentCloudSDKException var7) {
            mLogger.warn(var7.toString());
            handleError(var7.getMessage());
        }

    }

    private void checkStatus(int status, String from, String itemId) {
        switch (status) {
            case 1:
                if (from.equals("result")) {
                    String md5 = response.getShieldInfo().getShieldMd5();
                    String url = response.getShieldInfo().getAppUrl();
                    if (param.getDownloadType().equals("url")) {
                        mLogger.warn("reinforced apk download url:");
                        mLogger.warn(url);
                        success("reinforce success", "");
                    } else {
                        mLogger.warn("reinforce success, downloading");
                        download(md5, url, getDownFilePath(), "download reinforced apk success");
                    }
                }
                break;
            case 2:
                if (from.equals("create")) {
                    mLogger.warn("request success");
                    checkResult(itemId, param);
                } else if (from.equals("result")) {
                    try {
                        TimeUnit.SECONDS.sleep(5L);
                    } catch (InterruptedException var7) {
                        ErrorUtil.redQuit(ReturnCode.TOOLERROR);
                    }

                    checkResult(itemId, param);
                }
                break;
            case 3:
                if (from.equals("result")) {
                    int code = response.getShieldInfo().getShieldCode();
                    mLogger.warn("error code: " + code);
                } else {
                    CommonUtil.prettyJson(CommonUtil.adapter(ReturnCode.SHIELDERROR));
                }
            case 4:
                CommonUtil.prettyJson(CommonUtil.adapter(ReturnCode.SHIELDERROR));
            default:
                break;
        }

    }

    private void download(final String md5, String url, final String fileName, final String msg) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                try {
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
                    }

                    sink.flush();
                    sink.close();
                    source.close();
                    if (msg.equals("download reinforced apk success")) {
                        mLogger.warn("reinforced apk MD5: " + md5);
                    }

                    if (md5.toLowerCase().equals(CommonUtil.getFileMd5(down))) {
                        success(msg, fileName);
                    } else {
                        CommonUtil.prettyJson(adapter(ReturnCode.SIGNATURE));
                    }
                } catch (IOException var15) {
                    CommonUtil.prettyJson(adapter(ReturnCode.DOWNLOADPATH));
                }
            } else if (response.code() > 400 && response.code() < 500) {
                CommonUtil.prettyJson(adapter(ReturnCode.CLIENTERROR));
            } else {
                CommonUtil.prettyJson(adapter(ReturnCode.SERVERERROR));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void success(String msg, String fileName) {
        mLogger.warn(msg);
        if (msg.equals("update success")) {
            File file = new File(fileName);
            if (file.exists()) {
                file.renameTo(new File("ms-shield.jar"));
            }
        } else {
            mLogger.warn("reinforced apk need resign");
            mLogger.warn("reinforce and resigned apk must be test");
        }
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

    private void checkResult(String requestId, Param param) {
        mLogger.warn("reinforcing apk.......");
        Credential credential = new Credential(param.getSid(), param.getSkey());
        MsClient client = new MsClient(credential, "");
        DescribeShieldResultRequest resultRequest = new DescribeShieldResultRequest();
        resultRequest.setItemId(requestId);

        try {
            response = client.DescribeShieldResult(resultRequest);
            checkStatus(response.getTaskStatus(), "result", requestId);
        } catch (TencentCloudSDKException var8) {
            handleError(var8.getMessage());
        }

    }

    private void getResource() {
        try {
            Credential credential = new Credential(param.getSid(), param.getSkey());
            MsClient client = new MsClient(credential, "");
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
        } catch (TencentCloudSDKException var13) {
            handleError(var13.getMessage());
        }

    }

    private void getShieldPlan() {
        try {
            Credential credential = new Credential(param.getSid(), param.getSkey());
            MsClient client = new MsClient(credential, "");
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
        } catch (TencentCloudSDKException var12) {
            handleError(var12.getMessage());
        }

    }

    private void handleError(String message) {
        if (message.toLowerCase().contains("AuthFailure".toLowerCase())) {
            CommonUtil.prettyJson(adapter(ReturnCode.AUTHFAILURE));
        } else if (message.toLowerCase().contains("timed out".toLowerCase())) {
            CommonUtil.prettyJson(adapter(ReturnCode.TIMEOUT));
        } else {
            mLogger.warn(message);
        }
    }

    private static String adapter(Object obj) {
        EnumToString string = new EnumToString(obj);
        return (new Gson()).toJson(string);
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
