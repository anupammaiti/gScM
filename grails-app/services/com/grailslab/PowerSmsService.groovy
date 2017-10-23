package com.grailslab

import grails.converters.JSON
import grails.transaction.Transactional
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONObject

import javax.annotation.PostConstruct

class PowerSmsService {
    static transactional = false

    GrailsApplication grailsApplication
    OkHttpClient client

    @PostConstruct
    void init() {
        client = new OkHttpClient();
    }

    def sendSMSPost(String message, String cellNumber){
        def smsEnabled = grailsApplication.config.powersms.send.enabled
        if(smsEnabled) {
            try {
                RequestBody body = smsUrlBody(message, cellNumber)
                Request request = new Request.Builder()
                        .url(POWER_SMS_API_ENDPOINT+SEND_SMS_API)
                        .post(body)
                        .addHeader("content-type", "application/x-www-form-urlencoded")
                        .build();

                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObj = JSON.parse(jsonData)
                boolean isError = jsonObj.isError
                if (isError) {
                    return [isError: jsonObj.isError, errorMessage: jsonObj.message]
                }
                return [isError: jsonObj.isError, errorMessage: jsonObj.message, insertedSmsIds: jsonObj.insertedSmsIds]
            } catch (Exception ex) {
                log.error("SMS SEND FAIL: "+ex.getMessage())
                return ['isError': true, errorMessage:"Can't send message now due to server error. Please contact admin"]
            }
        } else {
            return ['isError': true, errorMessage:"Your message service isn't enabled. Please contact admin"]
        }
    }

    protected RequestBody smsUrlBody(String smsText, String mobileNos) {
        String userId = grailsApplication.config.powersms.api.userid
        String password = grailsApplication.config.powersms.api.password
        RequestBody formBody = new FormBody.Builder()
                .add("userId", userId)
                .add("password", password)
                .add("smsText", smsText)
                .add("commaSeperatedReceiverNumbers", mobileNos)
                .build();
        return formBody
    }
    public static final String SEND_SMS_API = "/sendsms"
    public static final String POWER_SMS_API_ENDPOINT = "https://powersms.banglaphone.net.bd/httpapi"
}
