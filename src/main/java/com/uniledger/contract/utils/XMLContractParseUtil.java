package com.uniledger.contract.utils;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.DocumentHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//============================================
//解析过程：
//  1. xml文件格式化输出
//  2. 识别xml文件中的component组件 和 line组件,并分别初始化到map结构中
//  3. 解析XML文件(按contract\componentTask顺序解析)到Json串
//============================================
//XML文件规则：
//  1. line的id解析：<object label="" id="5">
//  2. line的关联元素解析：<mxCell edge="1" parent="1" source="2" target="3">
//  3. component解析：<object label="查询用户账户" Ctype="Component_Task.Task_Enquiry" Caption="查询用户账户" Description="查询移动用户A账户是否有500元" Cname="enquiry_A" 。。。。>
//============================================
//元素解析规则：
//  1. 通过Ctype识别节点组件：
//     Start节点Ctype:Component_Contract            Enquiry节点:Component_Task.Task_Enquiry    Action节点:Component_Task.Task_Action
//     Decision节点      :Component_Task.Task_Decision    Plan节点          :Component_Task.Task_Plan
//     PreCondition,CompleteCondition,DiscardCondition: Component_Expression.Expression_Condition
//     DataSetterValueList: Component_Expression.Expression_Function
//     DataList: Component_Data.{根据HardConvType识别转化}
//  2. 常见类型：元素的Key: 接口中定义的字段名相同
//     数组类型：元素的Key为: 接口中定义的字段+[0-9],如
//     结构体定义：元素的Key为: 接口中结构名+属性名,如PreConditionCname
//     map结构：Meta前缀+Key名称，如MetaAttributeVersion
//     取值范围DataRange: 属性名+{From,To},如DataRangeFrom
//==============================================
public class XMLContractParseUtil {
	/**
	 * 转换合约描述xml  字符串到json 字符串
	 * @param str_xml 合约描述xml字符串
	 */
	public static String parseXMLToJsonFromString(String str_xml) throws Exception {
		StringBuffer str_json = new StringBuffer();
		str_json.append("{\"ContractBody\":{");
		if (str_xml == null || "".equals(str_xml)){
			str_json.append("}");
			return str_json.toString();
		}
		HashMap<String, String> map_components = new HashMap<String, String>();
		HashMap<String, String> map_component_names = new HashMap<String, String>();
		HashMap<String, String> map_lines = new HashMap<String, String>();
		//xml合约格式化
		try {
			Document document = DocumentHelper.parseText(str_xml);
			// 格式化输出格式
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			StringWriter writer = new StringWriter();
			// 格式化输出流
			XMLWriter xmlWriter = new XMLWriter(writer, format);
			// 将document写入到输出流
			xmlWriter.write(document);
			xmlWriter.close();
			StringReader reader = new StringReader(writer.toString());
			//读文件，将文件内容加载到Map中
			readToMap(reader, map_components, map_component_names, map_lines);

			//依据文件Map数据解析成JSON文件
			parseMapToJson(map_components, map_component_names, map_lines, str_json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		str_json.append("}}");
		return str_json.toString();


	}

	/**
	 * 转换合约描述xml 文件到 json字符串
	 *
	 * @param xmlContractOrFilepath xml合约字符串或文件路径
	 * @param isFile                是否文件，是文件的话需要读取文件解析
	 * @return jsonContractStr
	 */
	public static String parseXMLToJsonFromFile(String xmlContractOrFilepath, boolean isFile) {
		//todo default use xmlContractContent
		StringBuffer str_json = new StringBuffer();
		str_json.append("{\"ContractBody\":{");
		if (CommonUtils.isEmpty(xmlContractOrFilepath)){
			str_json.append("}");
			return str_json.toString();
		}
		HashMap<String, String> map_components = new HashMap<String, String>();
		HashMap<String, String> map_component_names = new HashMap<String, String>();
		HashMap<String, String> map_lines = new HashMap<String, String>();
		//xml合约格式化
		try {
			if (isFile) {
				//创建文件
				File xml_file = new File(xmlContractOrFilepath);
				//验证文件是否存在
				if (!xml_file.exists()) {
					System.out.println("file[" + xmlContractOrFilepath + "] not exist!");
					str_json.append("}");
					return str_json.toString();
				}
				//xml文件预处理,  文件格式化
				String xml_format_filepath = formatXmlFile(xmlContractOrFilepath);
				if (xml_format_filepath == null || "".equals(xml_format_filepath)) {
					System.out.println("file[" + xmlContractOrFilepath + "] format fail!");
					str_json.append("}");
					return str_json.toString();
				}
				FileReader fr = new FileReader(xml_format_filepath);
				//读文件，将文件内容加载到Map中
				readToMap(fr, map_components, map_component_names, map_lines);

			} else {
				String xmlFormatContract = formatXmlContract(xmlContractOrFilepath);
				if (CommonUtils.isEmpty(xmlFormatContract)) {
					System.out.println("[" + xmlFormatContract + "] format fail!");
					str_json.append("}");
					return str_json.toString();
				}

				StringReader stringReader = new StringReader(xmlFormatContract);

				//读文件，将文件内容加载到Map中
				readToMap(stringReader, map_components, map_component_names, map_lines);

			}
			//依据文件Map数据解析成JSON文件
			parseMapToJson(map_components, map_component_names, map_lines, str_json);
		} catch (Exception e) {
			e.printStackTrace();
		}
		str_json.append("}}");
		return str_json.toString();
	}

	/**
	 * 格式化xml
	 *
	 * @param xmlContract
	 * @return
	 * @throws Exception
	 */
	public static String formatXmlContract(String xmlContract) throws Exception {
		if ("".equals(xmlContract)) {
			return null;
		}

		SAXReader reader = new SAXReader();
		Document document = reader.read(new StringReader(xmlContract));
		String formatXML = null;
		XMLWriter writer = null;
		if (document != null) {
			try {
				StringWriter stringWriter = new StringWriter();
//				OutputFormat format = new OutputFormat(" ", true);
				OutputFormat format = OutputFormat.createPrettyPrint();
//				format.setEncoding("utf-8");
				writer = new XMLWriter(stringWriter, format);
				writer.write(document);
				writer.flush();
				formatXML = stringWriter.getBuffer().toString();
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return formatXML;
	}

	//读取xml文件中，将component和line信息读取到Map中
	private static void readToMap(Reader reader,
								  HashMap<String, String> map_components,
								  HashMap<String, String> map_component_names,
								  HashMap<String, String> map_lines) throws Exception {
		if (map_components == null) {
			map_components = new HashMap<String, String>();
		}
		if (map_lines == null) {
			map_lines = new HashMap<String, String>();
		}
//
		BufferedReader br = new BufferedReader(reader);
		String str_xml = "";
		int line_count = 0;
		int component_count = 0;
		String str_line_id = "";
		String sb = "";
		int s = 0;
		//while ((str_xml = br.readLine()) != null) {
		while ((s=br.read())!= -1 ){
			sb+=(char)s;
			if (s == '\n') {
				str_xml = sb;
				sb = "";
			} else {
				continue;
			}
			if ("".equals(str_xml)) {
				continue;
			} else if (Pattern.compile("<mxGraphModel ").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("<root>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("<mxCell .*/>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</mxCell>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</object>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("<mxGeometry ").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</root>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</mxGraphModel>").matcher(str_xml).find()) {
				continue;
			}

			//识别Line
			if (Pattern.compile("<mxCell .*id=\"[0-9]+\" .*source=\"[0-9]+\" .*target=\"[0-9]+\".*>").matcher(str_xml).find()) {
				str_xml = str_xml.replace("<mxCell ", "");
				str_xml = str_xml.replace(">", "");
				Pattern id_pattern = Pattern.compile(" id=\"[0-9]*\"");
				Matcher id_matcher = id_pattern.matcher(str_xml);
				if (id_matcher.find()) {
					line_count++;
					String id_keyvalue = id_matcher.group();
					str_line_id = id_keyvalue.trim().split("=")[1];
					map_lines.put(str_line_id, str_xml);
				}
				continue;
			}
			//识别component
			Pattern comp_pattern = Pattern.compile("<object .*label=\"[\\s\\S]+Cname=\"[\\s\\S]+>");
			Matcher comp_matcher = comp_pattern.matcher(str_xml);
			if (comp_matcher.find()) {
				component_count++;
				System.out.println(str_xml);
				str_xml = str_xml.replace("<object ", "");
				str_xml = str_xml.replace(">", "");
				Pattern id_pattern = Pattern.compile(" id=\"[0-9]*\"");
				Matcher id_matcher = id_pattern.matcher(str_xml);
				String id_str = "";
				if (id_matcher.find()) {
					String id_keyvalue = id_matcher.group();
					id_str = id_keyvalue.trim().split("=")[1];
					map_components.put(id_str, str_xml);
				}

				Pattern cname_pattern = Pattern.compile(" Cname=\"[a-zA-Z0-9_]*\"");
				Matcher cname_matcher = cname_pattern.matcher(str_xml);
				if (cname_matcher.find()) {
					String cname_keyvalue = cname_matcher.group();
					String cname_str = cname_keyvalue.trim().split("=")[1];
					map_component_names.put(id_str, cname_str);
				}
				continue;
			}
		}
		br.close();
		reader.close();
		if (line_count != map_lines.size()) {
			throw new Exception("Parse Line fail,xml_Line[" + line_count + "], map_Line[" + map_lines.size() + "]!");
		}
		if (component_count != map_components.size()) {
			throw new Exception("Parse Component fail,xml_component[" + component_count + "], map_component[" + map_components.size() + "]!");
		}
		System.out.println("==================================================");
		System.out.println("ReadFileToMap done, map_lines.size()=" + map_lines.size() + ", map_components.size()=" + map_components.size());
	}

	//Arga: p_str     待转化字符串
	private static String processSpecialStr(String p_str){
		if (p_str == null || "".equals(p_str)){
			return p_str;
		}
		p_str = p_str.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;", "\"").replaceAll("&apos;", "\'");
		return p_str;
	}

	private static String formatXmlFile(String input_file) throws Exception {
		if ("".equals(input_file)) {
			return null;
		}

		StringBuffer input_file_path = new StringBuffer();
		String input_file_name = "";
		String output_file_name = "";
		String[] filename_array = input_file.split("\\\\" + File.separator);
		String str_file_content = "";
		int filename_count = filename_array.length;
		for (int i = 0; i < filename_count - 1; i++) {
			input_file_path.append(filename_array[i]).append(File.separator);
		}
		input_file_name = filename_array[filename_count - 1];
		output_file_name = "format_" + input_file_name;

		File file = new File(input_file);
		try {
			FileInputStream in = new FileInputStream(file);
			int size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			str_file_content = new String(buffer, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (str_file_content == null || "".equals(str_file_content)) {
			return null;
		}

		SAXReader reader = new SAXReader();
		StringReader in = new StringReader(str_file_content);
		Document doc = reader.read(in);

		FileOutputStream out = new FileOutputStream(input_file_path + output_file_name);
		OutputFormat formater = OutputFormat.createPrettyPrint();
		formater.setEncoding("utf-8");
		XMLWriter writer = new XMLWriter(out, formater);
		writer.write(doc);
		writer.close();

		return input_file_path + output_file_name;
	}

	//读取xml文件中，将component和line信息读取到Map中
	public void ReadFileToMap(String xml_filepath,
							  HashMap<String, String> map_components,
							  HashMap<String, String> map_component_names,
							  HashMap<String, String> map_lines) throws Exception{
		if (xml_filepath==null || xml_filepath == "") {
			System.out.println("Param[xml_filepath] is null!");
			return;
		}
		if (map_components == null) {
			map_components = new HashMap<String, String>();
		}
		if (map_lines == null) {
			map_lines = new HashMap<String, String>();
		}
		FileReader fr = new FileReader(xml_filepath);
		BufferedReader br = new BufferedReader(fr);
		String str_xml = "";
		int line_count = 0;
		int component_count = 0;
		String str_line_id = "";
		while ((str_xml = br.readLine()) != null) {
			if ("".equals(str_xml)) {
				continue;
			} else if (Pattern.compile("<mxGraphModel ").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("<root>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</mxCell>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</object>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("<mxGeometry ").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</root>").matcher(str_xml).find()) {
				continue;
			} else if (Pattern.compile("</mxGraphModel>").matcher(str_xml).find()) {
				continue;
			}

			//识别Line
			if (Pattern.compile("<mxCell id=\"[0-9]+\" .*source=\"[0-9]+\" target=\"[0-9]+\">").matcher(str_xml).find()) {
				str_xml = str_xml.replace("<mxCell ", "");
				str_xml = str_xml.replace(">", "");
				Pattern id_pattern = Pattern.compile(" id=\"[0-9]*\"");
				Matcher id_matcher = id_pattern.matcher(str_xml);
				if (id_matcher.find()) {
					line_count++;
					String id_keyvalue = id_matcher.group();
					str_line_id = id_keyvalue.trim().split("=")[1];
					map_lines.put(str_line_id, str_xml);
				}
				continue;
			}
			//识别component
			Pattern comp_pattern = Pattern.compile("<object label=\"[\\s\\S]+Cname=\"[\\s\\S]+>");
			Matcher comp_matcher = comp_pattern.matcher(str_xml);
			if (comp_matcher.find()) {
				component_count++;
				System.out.println(str_xml);
				str_xml = str_xml.replace("<object ", "");
				str_xml = str_xml.replace(">", "");
				Pattern id_pattern = Pattern.compile(" id=\"[0-9]*\"");
				Matcher id_matcher = id_pattern.matcher(str_xml);
				String id_str = "";
				if (id_matcher.find()){
					String id_keyvalue = id_matcher.group();
					id_str = id_keyvalue.trim().split("=")[1];
					map_components.put(id_str, str_xml);
				}

				Pattern cname_pattern = Pattern.compile(" Cname=\"[a-zA-Z0-9_]*\"");
				Matcher cname_matcher = cname_pattern.matcher(str_xml);
				if (cname_matcher.find()){
					String cname_keyvalue = cname_matcher.group();
					String cname_str = cname_keyvalue.trim().split("=")[1];
					map_component_names.put(id_str, cname_str);
				}

				continue;
			}
		}
		br.close();
		fr.close();
		if (line_count != map_lines.size()) {
			throw new Exception("Parse Line fail,xml_Line["+line_count+"], map_Line["+map_lines.size()+"]!");
		}
		if (component_count != map_components.size()) {
			throw new Exception("Parse Component fail,xml_component["+component_count+"], map_component["+map_components.size()+"]!");
		}
		System.out.println("==================================================");
		System.out.println("ReadFileToMap done, map_lines.size()="+ map_lines.size()+ ", map_components.size()="+ map_components.size());
	}

	//根据XML组件map、XML线map解析成JSON串
	public static void parseMapToJson(HashMap<String, String> p_map_components,
							   HashMap<String, String> p_map_component_names,
							   HashMap<String, String> p_map_lines,
							   StringBuffer p_json){
		if(p_map_components == null || p_map_lines == null || p_map_component_names == null
				|| p_map_components.size() == 0 || p_map_lines.size() == 0 || p_map_component_names.size() == 0){
			System.out.println("Param[map_components or p_map_component_names or map_lines] is null!");
			return;
		}
		//解析NextTasks Map
		HashMap<String, String> map_nexttasks = new HashMap<String, String>();
		map_nexttasks = parseNextTasksMap(p_map_component_names, p_map_lines);
		//Test
		System.out.println("===========================================================");
		for (Map.Entry<String, String> entry : p_map_components.entrySet()) {
			System.out.println("component_key: " + entry.getKey() + ", component_value: " + entry.getValue());
		}

		//解析合约节点Start id
		//TODO 组件添加组件类型属性，默认给出值
		String str_startid = parseStartID(p_map_components);
		if (str_startid == null || "".equals(str_startid)) {
			System.out.println("Get Start Node Id fail, Error[startid is null]!");
			return;
		}

		//解析合约节点为JSON
		String str_prop_basic = parseContractBasicPropertys(p_map_components.get(str_startid));
		if (str_prop_basic != null && !"".equals(str_prop_basic)){
			p_json.append(str_prop_basic);
		}

		//解析合约节点Owners TO json
		String str_prop_owners = parseContractOwnersProperty(p_map_components.get(str_startid));
		if (str_prop_basic != null && !"".equals(str_prop_basic)){
			p_json.append(",").append(str_prop_owners);
		}

		//解析合约节点AssetsTo json
		String str_prop_assets = parseContractAssetsProperty(p_map_components.get(str_startid));
		if (str_prop_assets != null && !"".equals(str_prop_assets)){
			p_json.append(",").append(str_prop_assets);
		}

		//解析合约NextTasks Tojson
		String str_prop_nexttasks = parseContractNexttaskProperty(str_startid, map_nexttasks);
		if (str_prop_nexttasks != null && !"".equals(str_prop_nexttasks)){
			p_json.append(",").append(str_prop_nexttasks);
		}

		//解析合约中的Attribute mata属相
		p_json.append(", \"MetaAttribute\": {");
		//  Version字段
		String str_version = parseProperty(p_map_components.get(str_startid), " MetaAttributeVersion=\"[\\s\\S]*?\"");
		System.out.println("MetaDataVersion: " + str_version);
		p_json.append("\"Version\": \"" + str_version.replaceAll("\"", "") + "\"");
		//  Copyright字段
		String str_copyright = parseProperty(p_map_components.get(str_startid), " MetaAttributeCopyright=\"[\\s\\S]*?\"");
		System.out.println("MetaDataCopyright: " + str_copyright);
		p_json.append(", \"Copyright\": \"" + str_copyright.replaceAll("\"", "") + "\"");
		String str_metadata = parseMetaDataMapProperty(p_map_components.get(str_startid), "MetaAttribute");
		if (str_metadata != null && !"".equals(str_metadata)){
			p_json.append(",").append(str_metadata);
		}
		p_json.append("}");

		//解析TaskComponents JSON
		String str_contractcomponents = parseTaskComponents(str_startid, p_map_components, map_nexttasks);
		if (str_contractcomponents != null && !"".equals(str_contractcomponents)){
			p_json.append(",").append(str_contractcomponents);
		}

	}

	//======================================================================
	//获取后继节点Map
	private static HashMap<String, String> parseNextTasksMap(
			HashMap<String, String> p_map_component_names, HashMap<String, String> p_map_lines){
		HashMap<String, String> map_nexttasks = new HashMap<String, String>();
		if (p_map_lines == null || p_map_lines.size() == 0 ) {
			return map_nexttasks;
		}
		for (Map.Entry<String, String> entry : p_map_lines.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			String str_source = "";
			String str_target = "";
			String str_nexttask = "";
			//parse source
			Pattern source_pattern = Pattern.compile(" source=\"[0-9]*\"");
			Matcher source_matcher = source_pattern.matcher(entry.getValue());
			if (source_matcher.find()){
				String source_keyvalue = source_matcher.group();
				str_source = source_keyvalue.trim().split("=")[1];

			}
			//parse target
			Pattern target_pattern = Pattern.compile(" target=\"[0-9]*\"");
			Matcher target_matcher = target_pattern.matcher(entry.getValue());
			if (target_matcher.find()){
				String target_keyvalue = target_matcher.group();
				str_target = target_keyvalue.trim().split("=")[1];
			}

			str_nexttask = p_map_component_names.get(str_target);
			//TODO: 针对组件自己换线回到自己的暂时不支持，此处为了屏蔽掉组件异常回路问题
			if (str_source.equals(str_target)) {
				continue;
			}
			if (map_nexttasks.get(str_source) == null) {
				map_nexttasks.put(str_source, str_nexttask);
			} else{
				str_nexttask = map_nexttasks.get(str_source) + "," + str_nexttask;
			}
			map_nexttasks.put(str_source, str_nexttask);
		}
		System.out.println("============================================");
		System.out.println("ParseNextTasksMap done: ");
		for (Map.Entry<String, String> entry : map_nexttasks.entrySet()) {
			System.out.println("currtask_id: " + entry.getKey() + ", nexttasks: " + entry.getValue());
		}
		return map_nexttasks;
	}

	//获取起始节点的ID
	private static String parseStartID(HashMap<String, String> p_map_components){
		String str_startID = null;
		for (Map.Entry<String, String> entry : p_map_components.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			//parse Ctype
			Pattern ctype_pattern = Pattern.compile(" Ctype=\"[a-zA-Z_.]+\" ");
			String str_ctype = "";
			Matcher ctype_matcher = ctype_pattern.matcher(entry.getValue());
			if (ctype_matcher.find()){
				String ctype_keyvalue = ctype_matcher.group();
				str_ctype = ctype_keyvalue.trim().split("=")[1];
				if (str_ctype.equals("\"Component_Contract\"")){
					str_startID = entry.getKey();
					break;
				}
			}
		}
		System.out.println("============================================");
		System.out.println("Root Node ID: " + str_startID);
		return str_startID;
	}

	//解析属性的Value值
	private static String parseProperty(String str_xml, String str_reg){
		Pattern ctype_pattern = Pattern.compile(str_reg);
		String str_value = "";
		Matcher property_matcher = ctype_pattern.matcher(str_xml);
		if (property_matcher.find()){
			String property_keyvalue = property_matcher.group();
			String [] arr_string = property_keyvalue.trim().split("=");
			if (arr_string.length == 2){
				str_value = arr_string[1];
			} else {
				for(int i=1;i<arr_string.length;i++) {
					if (i==1){
						str_value = arr_string[i];
					}else {
						str_value = str_value + "=" + arr_string[i];
					}
				}
			}
		}
		return str_value.trim();
	}

	//解析数组属性的Value值
	private static String[] parseArrayProperty(String str_xml, String str_reg){
		Pattern array_pattern = Pattern.compile(str_reg);
		String []array_value = null;
		Matcher property_matcher = array_pattern.matcher(str_xml);
		StringBuffer str_property = new StringBuffer();
		int property_count = 0;
		while (property_matcher.find()){
			property_count++;
			String property_keyvalue = property_matcher.group();
			if (property_count!=1){
				str_property.append(",");
			}
			String [] arr_string = property_keyvalue.trim().split("=");
			String str_value = "";
			if (arr_string.length == 2){
				str_value = arr_string[1];
			} else {
				for(int i=1;i<arr_string.length;i++) {
					if (i==1){
						str_value = arr_string[i];
					}else {
						str_value = str_value + "=" + arr_string[i];
					}
				}
			}
			str_property.append(str_value.trim());
		}
		array_value = str_property.toString().split(",");
		return array_value;
	}

	//解析MapInputFormat的属性,如：MetaAttributeKey, MetributeValue
	private static String parseSpecialMapProperty(String str_xml, String str_reg_key, String str_reg_value){
		Pattern map_key_pattern = Pattern.compile(str_reg_key);
		Pattern map_value_pattern = Pattern.compile(str_reg_value);

		Matcher key_matcher = map_key_pattern.matcher(str_xml);
		Matcher value_matcher = map_value_pattern.matcher(str_xml);

		StringBuffer str_value = new StringBuffer();
		String tmp_key = "";
		String tmp_value = "";
		if (key_matcher.find()){
			String match_tmp = "";
			match_tmp = key_matcher.group();
			tmp_key = match_tmp.trim().split("=")[1].trim();
		}
		if (value_matcher.find()){
			String match_tmp = "";
			match_tmp = value_matcher.group();
			tmp_value = match_tmp.trim().split("=")[1].trim();
			if ("".equals(tmp_value)) {
				tmp_value = "\"\"";
			}
		}
		if (tmp_key == null || "".equals(tmp_key)) {
			return "";
		}
		str_value.append("\"" + tmp_key.replace("\"", "") + "\": \"" + tmp_value.replace("\"", "") + "\"");
		return str_value.toString();
	}

	//解析Map属性的Value值
	private static String[] parseMapProperty(String str_xml, String str_reg){
		Pattern map_pattern = Pattern.compile(str_reg);
		String []array_value = null;
		Matcher property_matcher = map_pattern.matcher(str_xml);
		StringBuffer str_value = new StringBuffer();
		int property_count = 0;
		while (property_matcher.find()){
			property_count++;
			String match_tmp = "";
			String tmp_key = "";
			String tmp_value = "";

			match_tmp = property_matcher.group();
			tmp_key = match_tmp.trim().split("=")[0].trim().replace("\"", "");
			tmp_value = match_tmp.trim().split("=")[1].trim().replace("\"", "");
			if (property_count != 1) {
				str_value.append("###");
			}
			str_value.append("\"" + tmp_key.replace("\"", "") + "\": \"" + tmp_value.replace("\"", "") + "\"");
		}
		array_value = str_value.toString().split("###");
		return array_value;
	}

	//获取合约Body基本属性
	private static String parseContractBasicPropertys(String p_strContract){
		StringBuffer str_return = new StringBuffer();
		//ContractId   Default
//		UUID uuid = UUID.randomUUID();
//		String str_contractid = uuid.toString();
		str_return.append("\"ContractId\": \"\"");
		//ContractState Default
		str_return.append(", \"ContractState\": \"Contract_Create\"");
		//Cname
		String str_cname = parseProperty(p_strContract, " Cname=\"[\\s\\S]*?\"");
		System.out.println("Cname: " + str_cname);
		str_return.append(", \"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
		//Ctype
		String str_ctype = parseProperty(p_strContract, " Ctype=\"[\\s\\S]*?\"");
		System.out.println("Ctype: " + str_ctype);
		str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
		//Caption
		String str_caption = parseProperty(p_strContract, " Caption=\"[\\s\\S]*?\"");
		System.out.println("Caption: " + str_caption);
		str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
		//Description
		String str_description = parseProperty(p_strContract, " Description=\"[\\s\\S]*?\"");
		System.out.println("Description: " + str_description);
		str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");
		//CreateTime
		String str_createtime = parseProperty(p_strContract, " CreateTime=\"[\\s\\S]*?\"");
		System.out.println("CreateTime: " + str_createtime);
		str_return.append(", \"CreateTime\": \"" + str_createtime.replaceAll("\"", "") + "\"");
		//Creator
		String str_creator = parseProperty(p_strContract, " Creator=\"[\\s\\S]*?\"");
		System.out.println("Creator: " + str_creator);
		str_return.append(", \"Creator\": \"" + str_creator.replaceAll("\"", "") + "\"");
		//StartTime
		String str_starttime = parseProperty(p_strContract, " StartTime=\"[\\s\\S]*?\"");
		System.out.println("StartTime: " + str_starttime);
		str_return.append(", \"StartTime\": \"" + str_starttime.replaceAll("\"", "") + "\"");
		//EndTime
		String str_endtime = parseProperty(p_strContract, " EndTime=\"[\\s\\S]*?\"");
		System.out.println("EndTime: " + str_endtime);
		str_return.append(", \"EndTime\": \""+str_endtime.replaceAll("\"", "")+"\"");
		System.out.println("============================================");
		System.out.println("Root Node Basic Property: " + str_return.toString());
		return str_return.toString();
	}

	//获取合约Body ContractOwners属性
	private static String parseContractOwnersProperty(String p_strContract) {
		StringBuffer str_return = new StringBuffer();
		//ContractOwners     []string
		str_return.append(" \"ContractOwners\":[");
		String[] array_owners = parseArrayProperty(p_strContract, " ContractOwners[0-9]*=\"[\\s\\S]*?\"");
		if (array_owners != null && array_owners.length != 0) {
			int array_length = array_owners.length;
			for (int i=0;i<array_length;i++) {
				if (i == array_length-1) {
					str_return.append(array_owners[i]);
				} else {
					str_return.append(array_owners[i] + ",");
				}
			}
		}
		str_return.append("]");
		System.out.println("============================================");
		System.out.println("Root Node ContractOwners: " + str_return.toString());
		return str_return.toString();
	}

	//获取合约Body ContractOriginal Assets属性
	private static String parseContractAssetsProperty(String p_strContract) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"ContractAssets\":[");
		int precondition_count = parseArrayPropertyCount("ContractAssets_Cname[0-9]*", p_strContract);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//AssetId string
			//TaskId   Default
			UUID uuid = UUID.randomUUID();
			String str_assetid = uuid.toString();
			str_return.append("\"AssetId\": \"" + str_assetid + "\"");
			//Cname string
			String str_cname = parseProperty(p_strContract, " ContractAssets_Cname=\"[\\s\\S]*?\"");
			System.out.println("ContractAssets_Cname: " + str_cname);
			str_return.append(", \"Name\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Caption string
			String str_caption = parseProperty(p_strContract, " ContractAssets_Caption=\"[\\s\\S]*?\"");
			System.out.println("ContractAssets_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
			//Description string
			String str_description = parseProperty(p_strContract, " ContractAssets_Description=\"[\\s\\S]*?\"");
			System.out.println("ContractAssets_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");
			//Unit    string
			String str_unit = parseProperty(p_strContract, " ContractAssets_Unit=\"[\\s\\S]*?\"");
			System.out.println("ContractAssets_Unit: " + str_unit);
			str_return.append(", \"Unit\": \"" + str_unit.replaceAll("\"", "") + "\"");
			//Amount   interface{}
			String str_amount = parseProperty(p_strContract, " ContractAssets_Amount=\"[\\s\\S]*?\"");
			if (str_amount == null || "".equals(str_amount.replaceAll("\"", ""))){
				str_amount = "0.0";
			}
			System.out.println("ContractAssets_Amount: " + str_amount);
			str_return.append(", \"Amount\": " + str_amount.replaceAll("\"", ""));
			//解析合约中的Attribute mata属相
			str_return.append(", \"MetaData\": {");
			String str_metadata = parseMetaDataMapProperty(p_strContract, "ContractAssets");
			if (str_metadata != null && !"".equals(str_metadata)) {
				str_return.append(str_metadata);
			}
			str_return.append("}");

			str_return.append("}");
		}

		str_return.append("]");
		System.out.println("============================================");
		System.out.println("Root Node ContractAssets: " + str_return.toString());
		return str_return.toString();
	}

	//获取合约Body ContractOriginal Metadata属性
	private static String parseMetaDataMapProperty(String p_strContract, String p_key){
		//p_key: 如MetaAttribute   MetaData
		StringBuffer meta_content = new StringBuffer();
		//MetaAttribute map[string]string
		String[] array_attributes_keys = parseMapProperty(p_strContract, " "+p_key+"Key[0-9]*=\"[\\s\\S]*?\"");
		if (array_attributes_keys != null && array_attributes_keys.length != 0) {
			int array_length = array_attributes_keys.length;
			for (int i = 0; i < array_length; i++) {
				if("".equals(array_attributes_keys[i])) {
					continue;
				}
				int tmp_idx = i + 1;
				String tmp_key_value = parseSpecialMapProperty(p_strContract, " "+p_key+"Key"+tmp_idx+"=\"[\\s\\S]*?\"", " "+p_key+"Value"+tmp_idx+"=\"[\\s\\S]*?\"");
				if (tmp_key_value == null || "".equals(tmp_key_value)) {
					continue;
				}
				if (i == 0 || "".equals(meta_content.toString())) {
					meta_content.append(tmp_key_value);
				} else {
					meta_content.append(", " + tmp_key_value);
				}
			}
		}
		if ("{}".equals(meta_content.toString())) {
			meta_content = null;
		}
		System.out.println("============================================");
		System.out.println("Root Node "+p_key+": " + meta_content.toString());
		return meta_content.toString();
	}

	//获取合约Body nexttasks属性
	private static String parseContractNexttaskProperty(String p_strID, HashMap<String, String> map_nexttasks) {
		StringBuffer str_return = new StringBuffer();
		//NextTasks          []string
		if (map_nexttasks.get(p_strID) == null) {
			str_return.append(" \"NextTasks\":[]");
		} else {
			str_return.append(" \"NextTasks\":[").append(map_nexttasks.get(p_strID)).append("]");
		}
		System.out.println("============================================");
		System.out.println("Root Node NextTasks: " + str_return.toString());
		return str_return.toString();
	}

	//获取合约Body ContractOriginal 任务组件属性
	private static String parseTaskComponents(String str_startId, HashMap<String, String> p_map_componentss, HashMap<String, String> map_nexttasks){
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"ContractComponents\":[");
		if (p_map_componentss == null || p_map_componentss.size() == 0) {
			System.out.println("ParseTaskComponents fail, Error[Param p_map_componentss is null or zero]!");
			str_return.append("]");
			return str_return.toString();
		}
		int component_count = 0;
		for (Map.Entry<String, String> entry : p_map_componentss.entrySet()) {
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			//获取组件XML ID
			String strComponentID = parseTaskComponentID(entry.getValue());
			if (strComponentID == null || "".equals(strComponentID)) {
				System.out.println("Get Component ID fail, Error[componentID is null]!");
				continue;
			} else if (strComponentID.equals(str_startId)){
				continue;
			}
			component_count++;
			if (component_count == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//获取任务基本属性
			String str_basic = parseTaskBasicPropertys(entry.getValue());
			if (str_basic != null && !"".equals(str_basic)) {
				str_return.append(str_basic);
			}
			//获取任务task precondition属性
			String str_precondition = parseTaskPreConditionProperty(entry.getValue());
			if (str_precondition != null && !"".equals(str_precondition)) {
				str_return.append(",").append(str_precondition);
			}
			//获取任务task completecondition属性
			String str_completecondition = parseTaskCompleteConditionProperty(entry.getValue());
			if (str_completecondition != null && !"".equals(str_completecondition)) {
				str_return.append(",").append(str_completecondition);
			}
			//获取任务task discardcondition属性
			String str_discardcondition = parseTaskDiscardConditionProperty(entry.getValue());
			if (str_discardcondition != null && !"".equals(str_discardcondition)) {
				str_return.append(",").append(str_discardcondition);
			}
			//获取任务task datavaluesetterlist属性
			String str_datavaluesetterlist = parseTaskDataValueSetterListProperty(entry.getValue());
			if (str_datavaluesetterlist != null && !"".equals(str_datavaluesetterlist)) {
				str_return.append(",").append(str_datavaluesetterlist);
			}
			//获取任务task datalist属性
			String str_datalist = parseTaskDataListProperty(entry.getValue());
			if (str_datalist != null && !"".equals(str_datalist)) {
				str_return.append(",").append(str_datalist);
			}
			//获取任务task metadata属性
			String str_metadata = parseMetaDataMapProperty(entry.getValue(), "MetaAttribute");
			if (str_metadata != null && !"".equals(str_metadata)) {
				str_return.append(",").append(str_metadata);
			}
			//获取任务task nexttasks属性
			String str_nexttasks = parseTaskNexttaskProperty(strComponentID, map_nexttasks);
			if (str_nexttasks != null && !"".equals(str_nexttasks)) {
				str_return.append(",").append(str_nexttasks);
			}
			// TODO full for json serialize 2017-06-07
//			str_return.append(",\"CandidateList\":{}");
			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}

	//===========================================================================
	//获取XML中描述的组件表示ID
	private static String parseTaskComponentID(String p_task){
		String str_componentID = null;
		//parse id
		Pattern id_pattern = Pattern.compile(" id=\"[0-9]+\"");
		Matcher id_matcher = id_pattern.matcher(p_task);
		if (id_matcher.find()){
			String id_keyvalue = id_matcher.group();
			str_componentID = id_keyvalue.trim().split("=")[1];
		}
		System.out.println("============================================");
		System.out.println("Task Component Node ID: " + str_componentID);
		return str_componentID.trim();
	}

	//获取任务task基本属性
	private static String parseTaskBasicPropertys(String p_task){
		StringBuffer str_return = new StringBuffer();
		//TaskId   Default
		UUID uuid = UUID.randomUUID();
		String str_taskid = uuid.toString();
		str_return.append("\"TaskId\": \""+str_taskid+"\"");
		//TaskExecuteIdx
		str_return.append(", \"TaskExecuteIdx\": 0");
		//TaskState Default
		str_return.append(", \"State\": \"TaskState_Dormant\"");

		//Cname
		String str_cname = parseProperty(p_task, " Cname=\"[\\s\\S]*?\"");
		System.out.println("Cname: " + str_cname);
		str_return.append(", \"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
		//Ctype
		String str_ctype = parseProperty(p_task, " Ctype=\"[\\s\\S]*?\"");
		System.out.println("Ctype: " + str_ctype);
		str_ctype = str_ctype.replaceAll("\"", "");
		str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
		//Caption
		String str_caption = parseProperty(p_task, " Caption=\"[\\s\\S]*?\"");
		System.out.println("Caption: " + str_caption);
		str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
		//Description
		String str_description = parseProperty(p_task, " Description=\"[\\s\\S]*?\"");
		System.out.println("Description: " + str_description);
		str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");

		if ("Component_Task.Task_Enquiry".equals(str_ctype)) {
			//SelectBranches []common.SelectBranchExpression
			//获取任务task precondition属性
			String str_selectranch = parseTaskSelectBranchesProperty(p_task);
			if (str_selectranch != null && !"".equals(str_selectranch)) {
				str_return.append(",").append(str_selectranch);
			}
		} else if ("Component_Task.Task_Action".equals(str_ctype)) {
			//获取任务task TaskList []string 属性(Action property)
		} else if ("Component_Task.Task_Decision".equals(str_ctype)) {
			//获取任务决策所需的属性
			//CandidateList  []DecisionCandidate
			String str_candidateList = parseCandidateList(p_task);
			System.out.println("DecisionCandidateList: " + str_candidateList);
			str_return.append(", \"CandidateList\": [").append(str_candidateList).append("]");
		} else if ("Component_Task.Task_Plan".equals(str_ctype)) {
			//获取任务task TaskList []string 属性(Plan property)
		}
		return str_return.toString();
	}

	private static String parseCandidateList(String p_task){
		StringBuffer str_return = new StringBuffer();
		int candidatelist_count = parseArrayPropertyCount("CandidateList_Cname[0-9]*", p_task);
		for (int i = 1; i <= candidatelist_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " CandidateList_Cname=\"[\\s\\S]*?\"");
			System.out.println("CandidateList_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Ctype
			String str_ctype = "Component_Task.Task_DecisionCandidate";
			str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " CandidateList_Caption=\"[\\s\\S]*?\"");
			System.out.println("CandidateList_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
			//Description
			String str_description = parseProperty(p_task, " CandidateList_Description=\"[\\s\\S]*?\"");
			System.out.println("CandidateList_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");

			//Text []string
			int text_count = parseArrayPropertyCount("CandidateList_Text[0-9]*", p_task);
			StringBuffer str_textbuffer = new StringBuffer();
			str_textbuffer.append(", \"Text\": [");
			for (int j = 1; j <= text_count; j++) {
				String str_text = parseProperty(p_task, " CandidateList_Text"+j+"=\"[\\s\\S]*?\"");
				if (j == 1) {
					str_textbuffer.append(str_text);
				} else {
					str_textbuffer.append(", " + str_text);
				}
			}
			str_textbuffer.append("]");
			str_return.append(str_textbuffer.toString());

			str_return.append("}");
		}
		return str_return.toString();
	}

	private static int parseArrayPropertyCount(String str_reg, String str_xml){
		Pattern array_pattern = Pattern.compile(str_reg);
		Matcher property_matcher = array_pattern.matcher(str_xml);
		int property_count = 0;
		while (property_matcher.find()) {
			property_count++;
		}
		return property_count;
	}

	//获取任务task SelectBranchs选择分支属性
	private static String parseTaskSelectBranchesProperty(String p_task) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"SelectBranches\":[");
		int branches_count = parseArrayPropertyCount("SelectBranchs_BranchExpressionStr[0-9]*", p_task);
		for (int i = 1; i <= branches_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//ExpressionStr
			String str_expression = parseProperty(p_task, " SelectBranchs_BranchExpressionStr" + i + "=\"[\\s\\S]*?\"");
			System.out.println("SelectBranchs_BranchExpressionStr: " + str_expression);
			str_return.append("\"BranchExpressionStr\": \"" +  processSpecialStr(str_expression.replaceAll("\"", "")) + "\"");
			//BranchExpressionValue
			String str_expressionvalue = parseProperty(p_task, " SelectBranchs_BranchExpressionValue" + i + "=\"[\\s\\S]*?\"");
			System.out.println("SelectBranchs_BranchExpressionValue: " + str_expressionvalue);
			str_return.append(", \"BranchExpressionValue\": \"" + str_expressionvalue.replaceAll("\"", "") + "\"");

			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}

	//获取任务task precondition属性
	private static String parseTaskPreConditionProperty(String p_task){
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"PreCondition\":[");
		int precondition_count = parseArrayPropertyCount("PreCondition_Cname[0-9]*", p_task);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " PreCondition_Cname=\"[\\s\\S]*?\"");
			System.out.println("PreCondition_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Ctype
			String str_ctype = parseProperty(p_task, " PreCondition_Ctype=\"[\\s\\S]*?\"");
			System.out.println("PreCondition_Ctype: " + str_ctype);
			str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " PreCondition_Caption=\"[\\s\\S]*?\"");
			System.out.println("PreCondition_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
			//Description
			String str_description = parseProperty(p_task, " PreCondition_Description=\"[\\s\\S]*?\"");
			System.out.println("PreCondition_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");
			//ExpressionStr    string
			String str_expressionstr = parseProperty(p_task, " PreCondition_ExpressionStr=\"[\\s\\S]*?\"");
			System.out.println("PreCondition_ExpressionStr: " + str_expressionstr);
			str_return.append(", \"ExpressionStr\": \"" + processSpecialStr(str_expressionstr.replaceAll("\"", ""))+"\"");
			// TODO full for json serialize 2017-06-07
//			str_return.append(",\"ExpressionResult\":{}");
			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}

	//获取任务task completecondition属性
	private static String parseTaskCompleteConditionProperty(String p_task) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"CompleteCondition\":[");
		int precondition_count = parseArrayPropertyCount("CompleteCondition_Cname[0-9]*", p_task);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " CompleteCondition_Cname=\"[\\s\\S]*?\"");
			System.out.println("CompleteCondition_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Ctype
			String str_ctype = parseProperty(p_task, " CompleteCondition_Ctype=\"[\\s\\S]*?\"");
			System.out.println("CompleteCondition_Ctype: " + str_ctype);
			str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " CompleteCondition_Caption=\"[\\s\\S]*?\"");
			System.out.println("CompleteCondition_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", ""))  + "\"");
			//Description
			String str_description = parseProperty(p_task, " CompleteCondition_Description=\"[\\s\\S]*?\"");
			System.out.println("CompleteCondition_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");
			//ExpressionStr    string
			String str_expressionstr = parseProperty(p_task, " CompleteCondition_ExpressionStr=\"[\\s\\S]*?\"");
			System.out.println("CompleteCondition_ExpressionStr: " + str_expressionstr);
			str_return.append(", \"ExpressionStr\": \"" + processSpecialStr(str_expressionstr.replaceAll("\"", "")) + "\"");
			// TODO full for json serialize 2017-06-07
//			str_return.append(",\"ExpressionResult\":{}");
			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}

	//获取任务task discardcondition属性
	private static String parseTaskDiscardConditionProperty(String p_task) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"DiscardCondition\":[");
		int precondition_count = parseArrayPropertyCount("DiscardCondition_Cname[0-9]*", p_task);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " DiscardCondition_Cname=\"[\\s\\S]*?\"");
			System.out.println("DiscardCondition_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Ctype
			String str_ctype = parseProperty(p_task, " DiscardCondition_Ctype=\"[\\s\\S]*?\"");
			System.out.println("DiscardCondition_Ctype: " + str_ctype);
			str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " DiscardCondition_Caption=\"[\\s\\S]*?\"");
			System.out.println("DiscardCondition_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", ""))+ "\"");
			//Description
			String str_description = parseProperty(p_task, " DiscardCondition_Description=\"[\\s\\S]*?\"");
			System.out.println("DiscardCondition_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", "")) + "\"");
			//ExpressionStr    string
			String str_expressionstr = parseProperty(p_task, " DiscardCondition_ExpressionStr=\"[\\s\\S]*?\"");
			System.out.println("DiscardCondition_ExpressionStr: " + str_expressionstr);
			str_return.append(", \"ExpressionStr\": \"" + processSpecialStr(str_expressionstr.replaceAll("\"", "")) + "\"");
			// TODO full for json serialize 2017-06-07
			str_return.append(",\"ExpressionResult\":{}");
			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}

	//获取任务task datalist属性
	private static String parseTaskDataListProperty(String p_task) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"DataList\":[");
		int precondition_count = parseArrayPropertyCount("DataList_Cname[0-9]*", p_task);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " DataList_Cname=\"[\\s\\S]*?\"");
			System.out.println("DataList_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " DataList_Caption=\"[\\s\\S]*?\"");
			System.out.println("DataList_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
			//Description
			String str_description = parseProperty(p_task, " DataList_Description=\"[\\s\\S]*?\"");
			System.out.println("DataList_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", ""))+ "\"");
			//Unit string
			String str_unit = parseProperty(p_task, " DataList_Unit=\"[\\s\\S]*?\"");
			System.out.println("DataList_Unit: " + str_unit);
			str_return.append(", \"Unit\": \"" + str_unit.replaceAll("\"", "") + "\"");
			//HardConvType string
			String str_hardConvType = parseProperty(p_task, " DataList_HardConvType=\"[\\s\\S]*?\"");
			str_hardConvType = str_hardConvType.replaceAll("\"", "").trim();
			System.out.println("DataList_HardConvType: " + str_hardConvType);
			str_return.append(", \"HardConvType\": \"" + str_hardConvType.replaceAll("\"", "") + "\"");

			//   根据录入的数据类型分别设置Value、DefaultValue、DataRange、Ctype等
			//DefaultValue interface{}
			String str_defaultValue = parseProperty(p_task, " DataList_DefaultValue=\"[\\s\\S]*?\"");
			System.out.println("DataList_DefaultValue: " + str_defaultValue);
			//DataRange
			String str_datarangefrom = parseProperty(p_task, " DataList_DataRangeFrom=\"[\\s\\S]*?\"");
			System.out.println("DataList_DataRangeFrom: " + str_datarangefrom);
			String str_datarangeto = parseProperty(p_task, " DataList_DataRangeTo=\"[\\s\\S]*?\"");
			System.out.println("DataList_DataRangeTo: " + str_datarangeto);
			//Ctype
			String str_ctype = parseProperty(p_task, " DataList_Ctype=\"[\\s\\S]*?\"");
			if (str_hardConvType.equals("Data_Numeric_Int")) {
				if (str_defaultValue == null || "".equals(str_defaultValue)){
					str_defaultValue = "0";
				}
				str_return.append(", \"DefaultValueInt\": " + str_defaultValue.replaceAll("\"", ""));
				if (str_datarangefrom == null || "".equals(str_datarangefrom) || str_datarangeto == null || "".equals(str_datarangeto)){
					str_return.append(", \"DataRangeInt\": []");
				}else {
					str_return.append(", \"DataRangeInt\": [" + str_datarangefrom.replaceAll("\"", "") + "," + str_datarangeto.replaceAll("\"", "") + "]");
				}
				str_ctype = str_ctype + ".Data_Numeric_Int";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			} else if  (str_hardConvType.equals("Data_Numeric_Uint")) {
				if (str_defaultValue == null || "".equals(str_defaultValue)){
					str_defaultValue = "0";
				}
				str_return.append(", \"DefaultValueUint\": " + str_defaultValue.replaceAll("\"", ""));

				if (str_datarangefrom == null || "".equals(str_datarangefrom) || str_datarangeto == null || "".equals(str_datarangeto)){
					str_return.append(", \"DataRangeUint\": []");
				}else {
					str_return.append(", \"DataRangeUint\": [" + str_datarangefrom.replaceAll("\"", "") + "," + str_datarangeto.replaceAll("\"", "") + "]");
				}
				str_ctype = str_ctype + ".Data_Numeric_Uint";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			} else if  (str_hardConvType.equals("Data_Numeric_Float")) {
				if (str_defaultValue == null || "".equals(str_defaultValue)){
					str_defaultValue = "0.0";
				}
				str_return.append(", \"DefaultValueFloat\": " + str_defaultValue.replaceAll("\"", "") );

				if (str_datarangefrom == null || "".equals(str_datarangefrom) || str_datarangeto == null || "".equals(str_datarangeto)){
					str_return.append(", \"DataRangeFloat\": []");
				}else {
					str_return.append(", \"DataRangeFloat\": [" + str_datarangefrom.replaceAll("\"", "") + "," + str_datarangeto.replaceAll("\"", "") + "]");
				}
				str_ctype = str_ctype + ".Data_Numeric_Float";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			} else if  (str_hardConvType.equals("Data_Bool")) {
				if (str_defaultValue == null || "".equals(str_defaultValue)){
					str_defaultValue = "false";
				}
				str_return.append(", \"DefaultValueBool\": " + str_defaultValue.replaceAll("\"", "") );
				str_ctype = str_ctype + ".Data_Bool";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			} else if  (str_hardConvType.equals("Data_Text")) {
				str_ctype = str_ctype + ".Data_String";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
				str_return.append(", \"DefaultValueString\": \"" + str_defaultValue.replaceAll("\"", "") + "\"");
			} else if (str_hardConvType.equals("Data_Date")) {
				str_ctype = str_ctype + ".Data_Date";
				System.out.println("DataList_Ctype: " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
				String str_format = parseProperty(p_task, " DataListFormat" + i + "=\"[\\s\\S]*?\"");
				System.out.println("DataListFormat: " + str_format);
				str_return.append(", \"Format\": \"" + str_defaultValue.replaceAll("\"", "") + "\"");
			}else{
				str_ctype = str_ctype + "." + str_hardConvType;
				System.out.println(str_hardConvType + ": " + str_ctype);
				str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			}
			//ModifyDate string
			//Mandatory bool
			//Category []string
			//Options map[string]int
			//Parent inf.IData
			// TODO full for json serialize 2017-06-07
			str_return.append(",\"Parent\":{}");
			str_return.append("}");
		}
		str_return.append("]");
		return str_return.toString();
	}

	//获取任务task datavaluesetterlist属性
	private static String parseTaskDataValueSetterListProperty(String p_task) {
		StringBuffer str_return = new StringBuffer();
		str_return.append("\"DataValueSetterExpressionList\":[");
		int precondition_count = parseArrayPropertyCount("DataValueSetterExpressionList_Cname[0-9]*", p_task);
		for (int i = 1; i <= precondition_count; i++) {
			if (i == 1) {
				str_return.append("{");
			} else {
				str_return.append(", {");
			}
			//Cname
			String str_cname = parseProperty(p_task, " DataValueSetterExpressionList_Cname=\"[\\s\\S]*?\"");
			System.out.println("DataValueSetterExpressionList_Cname: " + str_cname);
			str_return.append("\"Cname\": \"" + str_cname.replaceAll("\"", "") + "\"");
			//Ctype
			String str_ctype = parseProperty(p_task, " DataValueSetterExpressionList_Ctype=\"[\\s\\S]*?\"");
			if (str_ctype.equals("")) {
				str_ctype = "Component_Expression.Expression_Function";
			}
			System.out.println("DataValueSetterExpressionList_Ctype: " + str_ctype);
			str_return.append(", \"Ctype\": \"" + str_ctype.replaceAll("\"", "") + "\"");
			//Caption
			String str_caption = parseProperty(p_task, " DataValueSetterExpressionList_Caption=\"[\\s\\S]*?\"");
			System.out.println("DataValueSetterExpressionList_Caption: " + str_caption);
			str_return.append(", \"Caption\": \"" + processSpecialStr(str_caption.replaceAll("\"", "")) + "\"");
			//Description
			String str_description = parseProperty(p_task, " DataValueSetterExpressionList_Description=\"[\\s\\S]*?\"");
			System.out.println("DataValueSetterExpressionList_Description: " + str_description);
			str_return.append(", \"Description\": \"" + processSpecialStr(str_description.replaceAll("\"", ""))+ "\"");
			//ExpressionStr    string
			String str_expressionstr = parseProperty(p_task, " DataValueSetterExpressionList_ExpressionStr=\"[\\s\\S]*?\"");
			System.out.println("DataValueSetterExpressionList_ExpressionStr: " + str_expressionstr);
			str_return.append(", \"ExpressionStr\": \"" + processSpecialStr(str_expressionstr.replaceAll("\"", "")) + "\"");

			// TODO full for json serialize 2017-06-07
			str_return.append(",\"ExpressionResult\":{}");
			str_return.append("}");
		}

		str_return.append("]");
		return str_return.toString();
	}


	//获取任务task nexttasks属性
	private static String parseTaskNexttaskProperty(String p_strID, HashMap<String, String> map_nexttasks) {
		StringBuffer str_return = new StringBuffer();
		if (map_nexttasks.get(p_strID) == null) {
			str_return.append("\"NextTasks\": []");
		} else {
			str_return.append("\"NextTasks\": [").append(map_nexttasks.get(p_strID)).append("]");
		}

		System.out.println("============================================");
		System.out.println("Task Component Node Nexttasks: " + str_return.toString());
		return str_return.toString();
	}

	public static void main(String args[]){
		//System.out.println(XMLContractParseUtil.parseXMLToJsonFromFile("C:\\XingTools\\WorkTools\\Java\\jdk1.8.0_111\\bin\\format_Demo.xml", true));
        String str_xml = "<mxGraphModel dx=\"1426\" dy=\"825\" grid=\"1\" gridSize=\"10\" guides=\"1\" tooltips=\"1\" connect=\"1\" arrows=\"1\" fold=\"1\" page=\"1\" pageScale=\"1\" pageWidth=\"826\" pageHeight=\"1169\" background=\"#ffffff\"><root><mxCell id=\"0\"/><mxCell id=\"1\" parent=\"0\"/><mxCell id=\"2\" style=\"edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;exitX=1;exitY=0.5;jettySize=auto;orthogonalLoop=1;\" edge=\"1\" source=\"3\" target=\"5\" parent=\"1\"><mxGeometry relative=\"1\" as=\"geometry\"><Array as=\"points\"/></mxGeometry></mxCell><object label=\"开始\" Ctype=\"Component_Contract\" CreateTime=\"2017-09-01 15:47:33\" Cname=\"conract_transfer\" Caption=\"房屋交易合约\" Creator=\"F2P8cmiNbzr79QserzAh2LktZLdR6AgnNRfjQd6eMbB9\" Description=\"房屋交易合约\" StartTime=\"2017-08-14 17:34:26\" EndTime=\"2017-10-01 17:34:28\" ContractOwners1=\"3FyHdZVX4adfSSTg7rZDPMzqzM8k5fkpu43vbRLvEXLJ\" ContractAssets_Cname=\"asset_ham\" ContractAssets_Description=\"跨链资产\" ContractAssets_amount=\"1\" ContractAssets_unit=\"元\" id=\"3\"><mxCell style=\"ellipse;whiteSpace=wrap;html=1;fill=#006600;fillColor=#00CC00;0\" vertex=\"1\" typeId=\"Start\" parent=\"1\"><mxGeometry x=\"80\" y=\"110\" width=\"40\" height=\"40\" as=\"geometry\"/></mxCell></object><mxCell id=\"4\" style=\"edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;exitX=1;exitY=0.5;jettySize=auto;orthogonalLoop=1;\" edge=\"1\" source=\"5\" target=\"8\" parent=\"1\"><mxGeometry relative=\"1\" as=\"geometry\"/></mxCell><object label=\"转账\" Ctype=\"Component_Task.Task_Action\" PreCondition_Ctype=\"Component_Expression.Expression_Condition\" DiscardCondition_Ctype=\"Component_Expression.Expression_Condition\" CompleteCondition_Ctype=\"Component_Expression.Expression_Condition\" DataList_Ctype=\"Component_Data\" DataValueSetterExpressionList_ExpressionStr=\"FuncTransferWithTransInMultiChain(\\&quot;money#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#3000##house#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#500\\&quot;)\" Caption=\"转账\" Cname=\"task_action_transfer\" Description=\"转账\" DataValueSetterExpressionList_Cname=\"expression_function_transferFunc\" DataValueSetterExpressionList_Description=\"转账方法\" DataList_Cname=\"data_array_expression_function_transferFunc\" DataList_HardConvType=\"Data_Array\" DataList_Description=\"\" id=\"5\"><mxCell style=\"whiteSpace=wrap;html=1;fillColor=#90f;3\" typeId=\"Action\" vertex=\"1\" parent=\"1\"><mxGeometry x=\"190\" y=\"105\" width=\"50\" height=\"50\" as=\"geometry\"/></mxCell></object><mxCell id=\"6\" style=\"edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;exitX=1;exitY=0.5;entryX=0;entryY=0.5;jettySize=auto;orthogonalLoop=1;\" edge=\"1\" source=\"8\" target=\"9\" parent=\"1\"><mxGeometry relative=\"1\" as=\"geometry\"/></mxCell><mxCell id=\"7\" style=\"edgeStyle=orthogonalEdgeStyle;rounded=0;html=1;exitX=0.5;exitY=1;entryX=0.5;entryY=0;jettySize=auto;orthogonalLoop=1;\" edge=\"1\" source=\"8\" target=\"10\" parent=\"1\"><mxGeometry relative=\"1\" as=\"geometry\"/></mxCell><object label=\"查询\" Ctype=\"Component_Task.Task_Enquiry\" PreCondition_Ctype=\"Component_Expression.Expression_Condition\" DiscardCondition_Ctype=\"Component_Expression.Expression_Condition\" CompleteCondition_Ctype=\"Component_Expression.Expression_Condition\" DataValueSetterExpressionList_Ctype=\"Component_Expression.Expression_Function\" DataList_Ctype=\"Component_Data\" Caption=\"查询\" DataValueSetterExpressionList_ExpressionStr=\"FuncIsTransInMultiChain(data_array_expression_function_transferFunc.Value)\" Cname=\"task_enquiry_sel\" Description=\"查询是否入链\" SelectBranchs_BranchExpressionStr1=\"\" SelectBranchs_BranchExpressionValue1=\"\" DataList_Cname=\"data_array_expression_function_selFunc\" DataValueSetterExpressionList_Cname=\"expression_function_selFunc\" DataValueSetterExpressionList_Description=\"查询方法\" DataList_HardConvType=\"Data_Array\" id=\"8\"><mxCell style=\"rhombus;whiteSpace=wrap;html=1;fillColor=#f09;2\" typeId=\"Enquiry\" vertex=\"1\" parent=\"1\"><mxGeometry x=\"333\" y=\"105\" width=\"80\" height=\"50\" as=\"geometry\"/></mxCell></object><object label=\"解冻\" Ctype=\"Component_Task.Task_Action\" PreCondition_Ctype=\"Component_Expression.Expression_Condition\" DiscardCondition_Ctype=\"Component_Expression.Expression_Condition\" CompleteCondition_Ctype=\"Component_Expression.Expression_Condition\" DataList_Ctype=\"Component_Data\" Caption=\"解冻\" DataValueSetterExpressionList_ExpressionStr=\"FuncUnFreezeMutilChain(\\&quot;money#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#3000##house#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#500\\&quot;,data_array_expression_function_selFunc.Value)\" Cname=\"task_action_unfreeze\" Description=\"操作成功，解冻资产\" PreCondition_Cname=\"expression_condition_unfreezeCond\" DataValueSetterExpressionList_Cname=\"expression_function_unfreezeFunc\" PreCondition_ExpressionStr=\"data_array_expression_function_selFunc.Value.0 == true\" PreCondition_Description=\"解冻的前置条件\" DataValueSetterExpressionList_Description=\"解冻资产\" id=\"9\"><mxCell style=\"whiteSpace=wrap;html=1;fillColor=#90f;3\" typeId=\"Action\" vertex=\"1\" parent=\"1\"><mxGeometry x=\"500\" y=\"105\" width=\"50\" height=\"50\" as=\"geometry\"/></mxCell></object><object label=\"回滚\" Ctype=\"Component_Task.Task_Action\" PreCondition_Ctype=\"Component_Expression.Expression_Condition\" DiscardCondition_Ctype=\"Component_Expression.Expression_Condition\" CompleteCondition_Ctype=\"Component_Expression.Expression_Condition\" DataList_Ctype=\"Component_Data\" Caption=\"回滚\" DataValueSetterExpressionList_ExpressionStr=\"FuncRollBackMutilTrans(\\&quot;money#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#3000##house#EcWbt741xS8ytvKWEqCPtDu29sgJ1iHubHyoVvuAgc8W#5XAJvuRGb8B3hUesjREL7zdZ82ahZqHuBV6ttf3UEhyL#500\\&quot;,data_array_expression_function_selFunc.Value)\" Cname=\"task_action_rollback\" Description=\"操作失败，回滚成功数据\" PreCondition_Cname=\"expression_condition_rollbackCond\" PreCondition_Description=\"回滚前置条件\" PreCondition_ExpressionStr=\"data_array_expression_function_selFunc.Value.0 == false\" DataValueSetterExpressionList_Cname=\"expression_function_rollbackFunc\" DataValueSetterExpressionList_Description=\"回滚操作\" id=\"10\"><mxCell style=\"whiteSpace=wrap;html=1;fillColor=#90f;3\" typeId=\"Action\" vertex=\"1\" parent=\"1\"><mxGeometry x=\"348\" y=\"241\" width=\"50\" height=\"50\" as=\"geometry\"/></mxCell></object></root></mxGraphModel>";
        try {
			System.out.print(XMLContractParseUtil.parseXMLToJsonFromString(str_xml));
		}catch(Exception ex){
        	ex.printStackTrace();
		}
	}
}
