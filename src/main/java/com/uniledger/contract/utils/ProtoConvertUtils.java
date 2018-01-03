package com.uniledger.contract.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.uniledger.contract.dto.ContractContent;
import com.uniledger.protos.ProtoContract;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wxcsdb88 on 2017/5/4 19:56.
 */
public class ProtoConvertUtils {

    /**
     * Convert json to Gson string
     * @param json
     * @param serializeNulls if or not serialize the null value
     * @return
     */
    protected static String parseJsonWithStrategy(String json, boolean serializeNulls, boolean disableHtmlEscaping) {
        GsonBuilder gsonBuilder;
        if(serializeNulls && disableHtmlEscaping){
            gsonBuilder = new GsonBuilder().serializeNulls().disableHtmlEscaping();
        }else if(serializeNulls){
            gsonBuilder = new GsonBuilder().serializeNulls();
        }else if(disableHtmlEscaping){
            gsonBuilder = new GsonBuilder().disableHtmlEscaping();
        }else{
            gsonBuilder = new GsonBuilder();
        }
        Gson gson = gsonBuilder.create();
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = null;
        String jsonStr = null;
        try {
            jsonElement = jsonParser.parse(json);
            jsonStr = gson.toJson(jsonElement); //remove null value
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonStr;
    }

    /**
     * Convert json to Gson string, not serialize the null value
     *
     * @param json
     * @return
     */
    public static String parseJson(String json) {
        return parseJsonWithStrategy(json, false, true);
    }

    /**
     * Convert json to Gson string, serialize the null value!
     * For protobuf-java-format serialize the proto, you shouldnnt use this!
     *
     * @param json
     * @return
     */
    public static String parseJsonSerializeNulls(String json) {
        return parseJsonWithStrategy(json, true, true);
    }

    /**
     * Convert json to  com.google.protobuf.Message.Builder
     *
     * @param gsonStr
     * @param builder
     * @return
     */
    protected static Message.Builder jsonToBuilder(String gsonStr, Message.Builder builder) {
        try {
            JsonFormat.parser().merge(gsonStr, builder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    /**
     * Convert json string to  byte[]
     *
     * @param json
     * @param builder
     * @param <T>
     * @return
     */
    public static <T> byte[] jsonToBuilderToByte(String json, Message.Builder builder) {
        String gsonStr = parseJson(json);
        builder = jsonToBuilder(gsonStr, builder);
        byte[] bt = null;
        try {
            bt = builder.build().toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bt;
    }

    /**
     * Convert protoByte to message or proto
     *
     * @param bt
     * @param message
     * @return
     */
    public static Message protoByteToProto(byte[] bt, Message message) {
        Message obj = null;
        try {
            obj = message.getParserForType().parseFrom(bt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Convert protoByte to json string
     *
     * @param bt
     * @param message
     * @return
     */
    public static String protoByteToJson(byte[] bt, Message message) {
        Message obj = null;
        String jsonFormat = "";
        try {
            obj = message.getParserForType().parseFrom(bt);
            JsonFormat.Printer printer = JsonFormat.printer();
            jsonFormat = printer.omittingInsignificantWhitespace()
                    .preservingProtoFieldNames()
                    .includingDefaultValueFields() //todo loss filed should fill ful
                    .print(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonFormat;
    }

    /**
     * 根据xml合约 生成带id的json合约
     * @param xmlContractOrFilepath
     * @param isFile 是否从文件读取， 测试使用true
     * @param generateContractId 是否生成合约编号或id，为null时不存在者生存，为false不生成，为true则重新生成覆盖！
     * @param contractId 合约id
     * @return json格式完整合约，
     */
    public static String parseXMLToJson(String xmlContractOrFilepath, boolean isFile, Boolean generateContractId, String contractId){
        String jsonContract = "";
        try {
//            jsonContract = XMLContractParseUtil.parseXMLToJsonFromString(xmlContractOrFilepath);
            jsonContract = XMLContractParseUtil.parseXMLToJsonFromString(xmlContractOrFilepath);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        ProtoContract.Contract.Builder builder = ProtoContract.Contract.newBuilder();

        // todo need extra deal
        String regexContractId = "(?<=\"ContractId\":.\").*?(?=\")";
        Pattern pattern = Pattern.compile(regexContractId);
        Matcher matcher = pattern.matcher(jsonContract);
        String jsonContractId = "";
        while (matcher.find()){
            jsonContractId = matcher.group(0);
            System.out.println("regexContractId is " + jsonContractId);
        }
        if(generateContractId==null && CommonUtils.isEmpty(jsonContractId) || generateContractId!=null && generateContractId){
            if(CommonUtils.isEmpty(contractId)){
                jsonContractId = CommonUtils.generateSerialNo();
                System.out.println("Generate the contractId: " + jsonContractId);
            }
            jsonContract = jsonContract.replaceAll("\"ContractId\":\\s*\"[0-9]*\"", "\"ContractId\":\""+jsonContractId+"\"");
        }
        //todo xml转化json后数据补全，如果在用生成的json进行序列化反序列化则会出现字段丢失！！！！
        //todo 只能操作一次xml转json
        byte[] buf = ProtoConvertUtils.jsonToBuilderToByte(jsonContract, builder);
        ProtoContract.Contract message = ProtoContract.Contract.getDefaultInstance();
        String jsonFormat = ProtoConvertUtils.protoByteToJson(buf, message);
        System.out.println("full json from xml->json->proto->json:\n" + jsonFormat);
        return jsonContract;
    }

    public static String parseXMLToJson(String xmlContract, String contractId){
        String jsonContract = "";
        try {
            jsonContract = XMLContractParseUtil.parseXMLToJsonFromString(xmlContract);
        } catch(Exception ex){
            ex.printStackTrace();
        }
        ProtoContract.Contract.Builder builder = ProtoContract.Contract.newBuilder();
        //todo xml转化json后数据补全，如果在用生成的json进行序列化反序列化则会出现字段丢失！！！！
        //todo 只能操作一次xml转json
        byte[] buf = ProtoConvertUtils.jsonToBuilderToByte(jsonContract, builder);
        ProtoContract.Contract message = ProtoContract.Contract.getDefaultInstance();
        jsonContract = ProtoConvertUtils.protoByteToJson(buf, message);
        jsonContract = jsonContract.replaceAll("\"ContractId\":\\s*\"[0-9]*\"", "\"ContractId\":\""+contractId+"\"");
        return jsonContract;
    }

    /**
     * 根据xml合约获取合约内容
     * @param xmlContractOrFilepath 文件内容或路径
     * @param isFile 是否文件读取，测试用
     * @param contractId 合约编号，数据库获取
     * @return {@link ContractContent}
     */
    public static ContractContent parseXMLToContractContent(String xmlContractOrFilepath, boolean isFile, String contractId){
        String jsonContract = XMLContractParseUtil.parseXMLToJsonFromFile(xmlContractOrFilepath, isFile);
        ProtoContract.Contract.Builder builder = ProtoContract.Contract.newBuilder();
        byte[] buf = ProtoConvertUtils.jsonToBuilderToByte(jsonContract, builder);
        ProtoContract.Contract message = ProtoContract.Contract.getDefaultInstance();
        //todo deal
        ContractContent contractContent = new ContractContent();
        contractContent.setContractId(contractId);
        ProtoContract.ContractBody contractBody = message.getContractBody();
        contractContent.setCaption(contractBody.getCaption());
        return contractContent;
    }


    /**
     * 根据xml合约 生成带id的json合约
     * @param xmlContractOrFilepath
     * @param isFile 是否从文件读取， 测试使用true
     * @param generateContractId 是否生成合约编号或id，为null时不存在者生存，为false不生成，为true则重新生成覆盖！
     * @param contractId 合约id
     * @return json格式完整合约，
     */
    public static byte[] parseXMLToProtoByte(String xmlContractOrFilepath, boolean isFile, Boolean generateContractId, String contractId) {
        String jsonContract = parseXMLToJson(xmlContractOrFilepath, isFile, generateContractId, contractId);
        return  jsonContractToProtoByte(jsonContract);
    }

    /**
     * 已签名合约http传输转换为proto型 byte[]
     * @param jsonContract
     * @return
     */
    public static byte[] jsonContractToProtoByte(String jsonContract) {
        ProtoContract.Contract.Builder builder = ProtoContract.Contract.newBuilder();
        return ProtoConvertUtils.jsonToBuilderToByte(jsonContract, builder);
    }
}
