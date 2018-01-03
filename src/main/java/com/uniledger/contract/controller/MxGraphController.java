package com.uniledger.contract.controller;

import com.mxgraph.canvas.mxGraphicsCanvas2D;
import com.mxgraph.canvas.mxICanvas2D;
import com.mxgraph.reader.mxSaxOutputHandler;
import com.mxgraph.util.mxUtils;
import com.uniledger.contract.common.ResponseData;
import com.uniledger.contract.common.TokenUser;
import com.uniledger.contract.model.LocalContract;
import com.uniledger.contract.service.ContractService;
import com.uniledger.contract.service.ContractUserService;
import com.uniledger.contract.utils.DateUtils;
import com.uniledger.contract.utils.SessionUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by wxcsdb88 on 2017/5/23 13:46.
 */
@Api(protocols = "http,https", value = "/mxGraph", description = "mxGraph")
@Controller
@RequestMapping("/mxGraph")
public class MxGraphController {
    private static Logger log = LoggerFactory.getLogger(MxGraphController.class);

    @Autowired
    private ContractUserService contractUserService;
    @Autowired
    private ContractService contractService;

    @RequestMapping(value = "/saveMxgraphToImage", method = RequestMethod.POST,produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    public void saveMxgraphToImage(HttpServletRequest request, HttpServletResponse response,
                                   @ApiParam(name = "xml", required = false, value = "xml内容") @RequestParam(name = "xml", required = false) String xml,
                                   @ApiParam(name = "w", required = true, value = "宽度") @RequestParam(name = "w", required = true) int w,
                                   @ApiParam(name = "h", required = true, value = "宽度") @RequestParam(name = "h", required = true) int h,
                                   @ApiParam(name = "filename", required = false, value = "保存的文件名") @RequestParam(name = "filename", required = false) String filename) {

        BufferedImage image = mxUtils.createBufferedImage(w, h, Color.WHITE);
        // Creates handle and configures anti-aliasing
        Graphics2D g2 = image.createGraphics();
        mxUtils.setAntiAlias(g2, true, true);
        // Parses request into graphics canvas
        mxGraphicsCanvas2D gc2 = new mxGraphicsCanvas2D(g2);
        try {
            xml = URLDecoder.decode(xml, StandardCharsets.UTF_8.toString());
            System.out.println(xml);
            parseXmlSax(xml, gc2);
            response.setContentType("image/png");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + filename
                            + "\"; filename*=UTF-8''" + filename);
            response.setContentType("image/" + "png".toLowerCase());
            ImageIO.write(image, "png", response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void parseXmlSax(String xml, mxICanvas2D canvas)
            throws SAXException, ParserConfigurationException, IOException{
        // Creates SAX handler for drawing to graphics handle
        mxSaxOutputHandler handler = new mxSaxOutputHandler(canvas);

        // Creates SAX parser for handler
        XMLReader reader = SAXParserFactory.newInstance().newSAXParser()
                .getXMLReader();
        reader.setContentHandler(handler);

        // Renders XML data into image
        reader.parse(new InputSource(new StringReader(xml)));
    }

    @ApiOperation(value = "保存合约图", notes = "xml格式")
    @RequestMapping(value = "/saveMxGraphXml", method = RequestMethod.POST)
    public void saveMxgraphXml(HttpServletRequest request, HttpServletResponse response,
                               @ApiParam(name = "token", required = false, value = "标识") @RequestParam(name = "token", required = false) String token,
                               @ApiParam(name = "mime", required = false, value = "mime类型") @RequestParam(name = "mime", required = false) String mime,
                               @ApiParam(name = "xml", required = false, value = "xml内容") @RequestParam(name = "xml", required = false) String xml,
                               @ApiParam(name = "format", required = false, value = "标识", defaultValue = "xml") @RequestParam(name = "format", required = false, defaultValue = "xml") String format,
                               @ApiParam(name = "filename", required = false, value = "保存的文件名") @RequestParam(name = "filename", required = false) String filename) {

        byte[] data = null;
        try {
            boolean localSave = false;
            if (xml != null) {
                xml = URLDecoder.decode(xml, StandardCharsets.ISO_8859_1.toString());
                localSave = updateLocalContract(request, xml, false);
                data = xml.getBytes(StandardCharsets.ISO_8859_1.toString());

            } else {
                throw new Exception("xml content is null");
            }
            if (format == null) {
                format = "xml";
            }
            mime = "application/xml";

            if (filename != null && filename.length() > 0
                    && !filename.toLowerCase().endsWith(".svg")
                    && !filename.toLowerCase().endsWith(".html")
                    && !filename.toLowerCase().endsWith(".png")
                    && !filename.toLowerCase().endsWith("." + format)) {
                filename += "." + format;
            }
            response.setStatus(HttpServletResponse.SC_OK);
            if (filename != null) {
                response.setContentType(mime);
                response.setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename
                                + "\"; filename*=UTF-8''" + filename);
            } else if (mime.equals("image/svg+xml")) {
                response.setContentType("image/svg+xml");
            } else {
                // Required to avoid download of file
                response.setContentType("text/plain");
            }
            response.setStatus(HttpServletResponse.SC_OK);

            OutputStream out = response.getOutputStream();
            out.write(data);
            out.close();

        } catch (Exception e) {
            log.error("Error parsing xml contents : " + xml
                    + System.getProperty("line.separator")
                    + "Original stack trace : " + e.getMessage());
        }
    }

    /**
     * 自动保存操作
     *
     * @param request
     * @param response
     * @param token
     * @param mime
     * @param xml
     * @param format
     * @param filename
     * @return
     */
    @ApiOperation(value = "自动保存合约图", notes = "")
    @ApiResponses({
            @ApiResponse(code = 200, message = "自动保存成功"),
            @ApiResponse(code = 400, message = "参数错误"),
            @ApiResponse(code = 500, message = "服务器异常或操作失败"),
    })
    @RequestMapping(value = "/autoSaveMxGraphXml", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public ResponseData autoSaveMxgraphXml(HttpServletRequest request, HttpServletResponse response,
                                           @ApiParam(name = "token", required = true, value = "标识") @RequestParam(name = "token", required = true) String token,
                                           @ApiParam(name = "mime", required = true, value = "mime类型") @RequestParam(name = "mime", required = true) String mime,
                                           @ApiParam(name = "xml", required = true, value = "xml内容") @RequestParam(name = "xml", required = true) String xml,
                                           @ApiParam(name = "format", required = true, value = "标识", defaultValue = "xml") @RequestParam(name = "format", required = true, defaultValue = "xml") String format,
                                           @ApiParam(name = "filename", required = true, value = "保存的文件名") @RequestParam(name = "filename", required = true) String filename) {

        ResponseData<Boolean> responseData = new ResponseData<>();
        try {
            boolean localSave = false;
            if (xml != null) {
                xml = URLDecoder.decode(xml, StandardCharsets.ISO_8859_1.toString());
                localSave = updateLocalContract(request, xml, true);
            } else {
                responseData.setBadRequest("xml content is null");
            }
            responseData.setData(localSave);
            return responseData;
        } catch (Exception e) {
            log.error("Error parsing xml contents : " + xml
                    + System.getProperty("line.separator")
                    + "Original stack trace : " + e.getMessage());
            responseData.setException("Error parsing xml contents");
            return responseData;
        }
    }


    /**
     * @param xmlContract xml合约内容
     * @param isAutoSave  是否自动保存操作
     * @return
     */
    private boolean updateLocalContract(HttpServletRequest request, String xmlContract, boolean isAutoSave) {
        LocalContract localContract = new LocalContract();
//        localContract.setName(); //todo
//        localContract.setContractId(); //todo
//        localContract.setUpdateTime(DateUtils.currentTimestamp());
//        localContract.setExecuteTime(); //todo
        localContract.setExecuteStatus(Short.parseShort("-1"));
        localContract.setStatus(Short.parseShort("-1"));
        TokenUser currentUser = SessionUtil.getCurrentUser(request);
        localContract.setContractContent(xmlContract);
//        localContract.setCaption(); //todo
        localContract.setCreateTime(DateUtils.currentDateTime());
        localContract.setCreateUserId(currentUser.getId());
        localContract.setCreateUserName(currentUser.getUsername());
        localContract.setContractContent(xmlContract);
        return contractService.updateLocalContract(localContract);
    }
}
