package com.diplom.work.svc;

import com.diplom.work.controller.ControllerUtils;
import com.diplom.work.core.dto.CallInfo;
import com.diplom.work.core.dto.GetRecord;
import com.diplom.work.exceptions.SettingsNotFound;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class CallServiceTest {
    public static final String IP_ADDRESS = "127.0.0.1";
    private final RequestService requestService = mock(RequestService.class);
    private final CallService callService = new CallService(requestService);
    private String SESSION_ID;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        SESSION_ID = ControllerUtils.randomString(32);
    }

    @Test
    public void getCallInfoBySessionID() throws SettingsNotFound, JsonProcessingException {
        CallInfo callInfo = new CallInfo();
        CallInfo.Info info = new CallInfo.Info();
        info.setCall_type(1);
        info.setAnswering_pin("123");
        info.setDest_number("123457");
        info.setIs_record(true);
        info.setIs_fax(false);
        info.setIs_voicemail(false);
        info.setDirection(2);
        info.setDuration(60);
        info.setOrig_number("99991224");
        callInfo.setResult(0);
        callInfo.setInfo(info);
        callInfo.setResultMessage("test Message");
        String body = objectMapper.createObjectNode().put("session_id", SESSION_ID).toString();
        ResponseEntity<String> response = ResponseEntity.ok(gson.toJson(callInfo,CallInfo.class));
        Mockito
                .doReturn(response)
                .when(requestService)
                .postRequest(CallService.CALL_INFO_METHOD_NAME, body);
        CallInfo callInfoBySessionID = callService.getCallInfoBySessionID(SESSION_ID);
        assertEquals(callInfo, callInfoBySessionID);
    }

    @Test
    public void getRecordBySessionID() throws SettingsNotFound, JsonProcessingException {
        GetRecord getRecord = new GetRecord();
        getRecord.setResult("0");
        getRecord.setResultMessage("test Message");
        getRecord.setUrl("test url");
        String body = objectMapper.createObjectNode()
                .put("session_id", SESSION_ID).put("ip_adress", IP_ADDRESS).toString();
        ResponseEntity<String> response = ResponseEntity.ok(gson.toJson(getRecord,GetRecord.class));
        Mockito
                .doReturn(response)
                .when(requestService)
                .postRequest(CallService.GET_RECORD_METHOD_NAME, body);
        GetRecord recordBySessionID = callService.getRecordBySessionID(SESSION_ID, IP_ADDRESS);
        assertEquals(getRecord, recordBySessionID);
    }
}