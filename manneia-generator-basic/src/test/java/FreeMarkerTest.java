import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 指定模板文件所在路径
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // 指定字符集编码
        configuration.setDefaultEncoding("utf-8");
        // 创建模板对象,加载指定模板
        Template template = configuration.getTemplate("web.html.ftl");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("currentYear", 2023);
        List<Map<String, Object>> menuItems = new ArrayList<>();
        Map<String, Object> menuItem = new HashMap<>();
        menuItem.put("url", "https://github.com/manneia?tab=repositories");
        menuItem.put("label", "编程导航");
        menuItems.add(menuItem);
        dataModel.put("menuItems", menuItems);

        Writer out = new FileWriter("web.html");
        template.process(dataModel, out);
        out.close();
    }
}
