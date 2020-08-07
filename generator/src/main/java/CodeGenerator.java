import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author heyi
 */
public class CodeGenerator {

    /**
     * 配置数据库IP
     */
    private static final String DB_HOST = "127.0.0.1";

    /**
     * 配置数据库端口
     */
    private static final int DB_PORT = 3306;

    /**
     * 配置数据库名
     */
    private static final String DB_NAME = "bbf";

    /**
     * 配置用户名
     */
    private static final String DB_USER = "kaifa";

    /**
     * 配置数据库密码
     */
    private static final String DB_PWD = "123456";

    /**
     * 设置模板
     */
    private static final String ENTITY_VM = "/template/entity.java.vm";
    private static final String CONTROLLER_VM = "/template/controller.java.vm";
    private static final String SERVICE_VM = "/template/service.java.vm";
    private static final String SERVICE_IMPL_VM = "template/serviceImpl.java.vm";
    private static final String MAPPER_VM = "/template/mapper.java.vm";
    private static final String XML_VM = "/template/mapper.xml.vm";

    /**
     * 生成相应类，如需生成 则设置为 true 若已生成且不需要再次生成 则将其修改为false 避免代码被覆盖
     * 建议第一次运行都生成，都设置为 true , 之后只需将 entity 设置为true 即可，
     * 一般 entity 只是新增字段 不会去修改 其余如果有小修改，请手动修改
     * 如，重新生成 entity 后，id 规则 auto 还是 input 以及 修改字段后 xml 中的basemap 字段请手动添加下
     */
    private static final boolean CREATE_CONTROLLER = true;
    private static final boolean CREATE_SERVICE = true;
    private static final boolean CREATE_SERVICE_IMPL = true;
    private static final boolean CREATE_MAPPER = true;
    private static final boolean CREATE_XML = true;
    private static final boolean CREATE_ENTITY = true;

    public static void main(String[] args) {
        String[] models = {"repository", "business", "web"};
        for (String model : models) {
            generate(model);
        }
    }

    // =======================private method=======================

    private static void generate(String model) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 获取项目目录
        String projectPath = System.getProperty("user.dir");
        //生成文件的位置
//        String outputDir = projectPath + "/" + model + "/src/main/java";
        String outputDir = projectPath + "/generator/temp/" + model + "/src/main/java";
        gc.setOutputDir(outputDir);

        gc.setFileOverride(true);
        // 生成文件的作者名称
        gc.setAuthor("auto");
        // 是否打开输出目录
        gc.setOpen(false);
        gc.setActiveRecord(false);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(false);
        gc.setServiceName("%sService");

        // 设置为date类型
        gc.setDateType(DateType.ONLY_DATE);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?characterEncoding=utf8&useSSL=false&serverTimezone=UTC&rewriteBatchedStatements=true");
        // 设置数据库驱动数据库版本是mysql5.7及以上
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        // mysql5.6以下的驱动
        // dsc.setDriverName("com.mysql.jdbc.Driver");

        // 数据库用户名
        dsc.setUsername(DB_USER);
        // 数据库密码
        dsc.setPassword(DB_PWD);

        mpg.setDataSource(dsc);

        // 包配置
        //外层包名
        String packageName = "fun.littlecc." + model;
        PackageConfig pc = new PackageConfig();
        pc.setXml("mapper");
        pc.setService("service");
        pc.setEntity("entity");
        pc.setParent(packageName);
        mpg.setPackageInfo(pc);

        // 自定义配置
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        // 初始化默认模板
        initVM(templateConfig);

        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {

                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        if (model.equals("repository")) {
            // 在该模块下不需要生成的需要设置为null 下同
            templateConfig.setController(null);
            templateConfig.setService(null);
            templateConfig.setServiceImpl(null);
            // templateConfig 默认生成xml路径是不在resource下面的 所以需要下面的focList 自定义
            templateConfig.setXml(null);

            if (!CREATE_ENTITY) {
                templateConfig.setEntity(null);
            }

            focList.add(new FileOutConfig(XML_VM) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    // 自定义输入文件名称
                    return projectPath + "/generator/temp/repository/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                }
            });
        }

        if (model.equals("business")) {
            templateConfig.setEntity(null);
            templateConfig.setController(null);
            templateConfig.setMapper(null);
            templateConfig.setXml(null);
        }

        if (model.equals("web")) {
            templateConfig.setEntity(null);
            templateConfig.setMapper(null);
            templateConfig.setXml(null);
            templateConfig.setServiceImpl(null);
            templateConfig.setService(null);
            templateConfig.setController("/template/controller.java.vm");
        }


        // 使用自定义模板，不想要生成就设置为null,如果不设置null会使用默认模板
        checkCreate(templateConfig, focList);
        cfg.setFileOutConfigList(focList);
        mpg.setTemplate(templateConfig);
        mpg.setCfg(cfg);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // 指定生成表名
//        strategy.setInclude("tb_team_award_record");
        strategy.setExclude("PDMAN_DB_VERSION");
        strategy.setVersionFieldName("version");
        // 配置表前缀
        strategy.setTablePrefix("tb_");
        // 设置返回接口类型控制器
        strategy.setRestControllerStyle(true);
        // 实体继承超类
        strategy.setSuperEntityClass("fun.littlecc.repository.entity.support.BaseSupportEntity");
        // mapper 继承超类
        strategy.setSuperMapperClass("fun.littlecc.repository.mapper.support.BaseSupportMapper");
        // service 继承超类
        strategy.setSuperServiceClass("fun.littlecc.business.service.BaseService");
        // serviceImpl 继承超类
        strategy.setSuperServiceImplClass("fun.littlecc.business.service.impl.BaseServiceImpl");
        // entity使用lombok
        strategy.setEntityLombokModel(true);
        mpg.setStrategy(strategy);

        // 设置vm引擎 pom 中需依赖 velocity
        mpg.setTemplateEngine(new VelocityTemplateEngine());
        mpg.execute();
    }

    /**
     * 初始化模板信息
     *
     * @param tc
     */
    private static void initVM(TemplateConfig tc) {
        if (StringUtils.isNotEmpty(ENTITY_VM)) {
            tc.setEntity(ENTITY_VM);
        }
        if (StringUtils.isNotEmpty(MAPPER_VM)) {
            tc.setMapper(MAPPER_VM);
        }
        if (StringUtils.isNotEmpty(SERVICE_IMPL_VM)) {
            tc.setServiceImpl(SERVICE_IMPL_VM);
        }
        if (StringUtils.isNotEmpty(SERVICE_VM)) {
            tc.setService(SERVICE_VM);
        }
        if (StringUtils.isNotEmpty(XML_VM)) {
            tc.setXml(XML_VM);
        }
        if (StringUtils.isNotEmpty(CONTROLLER_VM)) {
            tc.setController(CONTROLLER_VM);
        }
    }

    /**
     * 检查是否需要生成代码
     *
     * @param tc
     * @param focList
     */
    private static void checkCreate(TemplateConfig tc, List<FileOutConfig> focList) {
        if (!CREATE_ENTITY) {
            tc.setEntity(null);
        }
        if (!CREATE_MAPPER) {
            tc.setMapper(null);
        }
        if (!CREATE_CONTROLLER) {
            tc.setController(null);
        }
        if (!CREATE_SERVICE) {
            tc.setService(null);
        }
        if (!CREATE_SERVICE_IMPL) {
            tc.setServiceImpl(null);
        }
        if (!CREATE_XML) {
            tc.setXml(null);
            focList.clear();
        }
    }
}
