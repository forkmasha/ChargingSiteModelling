package simulationParameters;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XmlParametersBuilder {
    private BufferedWriter writer;
    private String mainNode = "";
    private String groupName = "";
    public XmlParametersBuilder(BufferedWriter writer, String mainNode) {
        this.mainNode = mainNode;
        this.writer = writer;
    }

    public XmlParametersBuilder xmlStart() throws IOException {
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        writer.write("<" + mainNode + ">\n");
        return this;
    }

    public XmlParametersBuilder writeXmlEnd() throws IOException {
        writer.write("</" + mainNode + ">");
        return this;
    }

    public XmlParametersBuilder firstLevel(String nodeName, Object nodeValue) throws IOException {
        writer.write("    <" + nodeName + ">" + nodeValue + "</" + nodeName + ">\n");
        return this;
    }

    public XmlParametersBuilder firstLevel(String nodeName, Date nodeValue) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String currentDateTime = dateFormat.format(new Date());
        writer.write("    <" + nodeName + ">" + currentDateTime + "</" + nodeName + ">\n");
        return this;
    }

    public XmlParametersBuilder groupStart(String nodeName) throws IOException {
        groupName = nodeName;
        writer.write("    <" + nodeName + ">\n");
        return this;
    }

    public XmlParametersBuilder groupEnd() throws IOException {
        writer.write("    </" + groupName + ">\n");
        return this;
    }

    public XmlParametersBuilder childLevel(String nodeName, Object nodeValue) throws IOException {
        writer.write("        <" + nodeName + ">" + nodeValue + "</" + nodeName + ">\n");
        return this;
    }
}