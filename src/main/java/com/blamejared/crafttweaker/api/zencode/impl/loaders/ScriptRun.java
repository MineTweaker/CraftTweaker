package com.blamejared.crafttweaker.api.zencode.impl.loaders;

import com.blamejared.crafttweaker.CraftTweaker;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerRegistry;
import com.blamejared.crafttweaker.api.ScriptLoadingOptions;
import com.blamejared.crafttweaker.api.zencode.brackets.CTRegisterBEPEvent;
import com.blamejared.crafttweaker.api.zencode.brackets.IgnorePrefixCasingBracketParser;
import com.blamejared.crafttweaker.api.zencode.brackets.ValidatedEscapableBracketParser;
import com.blamejared.crafttweaker.api.zencode.impl.native_types.CrTJavaNativeConverterBuilder;
import com.blamejared.crafttweaker.impl.logger.GroupLogger;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;
import org.openzen.zencode.java.ScriptingEngine;
import org.openzen.zencode.java.module.JavaNativeModule;
import org.openzen.zencode.shared.CompileException;
import org.openzen.zencode.shared.SourceFile;
import org.openzen.zenscript.codemodel.FunctionParameter;
import org.openzen.zenscript.codemodel.HighLevelDefinition;
import org.openzen.zenscript.codemodel.ScriptBlock;
import org.openzen.zenscript.codemodel.SemanticModule;
import org.openzen.zenscript.formatter.FileFormatter;
import org.openzen.zenscript.formatter.ScriptFormattingSettings;
import org.openzen.zenscript.lexer.ParseException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class ScriptRun {
    
    private final ScriptLoadingOptions scriptLoadingOptions;
    private final SourceFile[] sourceFiles;
    private final ScriptingEngine scriptingEngine;
    private IgnorePrefixCasingBracketParser bep;
    
    public ScriptRun(ScriptLoadingOptions scriptLoadingOptions, SourceFile[] sourceFiles) {
        this.scriptLoadingOptions = scriptLoadingOptions;
        this.sourceFiles = sourceFiles;
        
        //Init Engine
        this.scriptingEngine = new ScriptingEngine(CraftTweakerAPI.logger);
        this.scriptingEngine.debug = CraftTweakerAPI.DEBUG_MODE;
    }
    
    public LoaderActions getLoaderActions() {
        return LoaderActions.getActionForLoader(scriptLoadingOptions.getLoaderName());
    }
    
    public boolean isFirstRun() {
        return getLoaderActions().isFirstRun();
    }
    
    public ScriptingEngine getEngine() {
        return scriptingEngine;
    }
    
    public void reload() {
        if(scriptLoadingOptions.isExecute()) {
            getLoaderActions().reload();
        }
        
        //Unless another mod messes up, that should always be a GroupLogger
        if(CraftTweakerAPI.logger instanceof GroupLogger) {
            ((GroupLogger) CraftTweakerAPI.logger).getPreviousMessages().clear();
        } else {
            final String message = "Internal Error: Expected Logger to be a GroupLogger, but found '%s'. This is most likely an issue with a CrT addon!";
            final String loggerClassName = CraftTweakerAPI.logger.getClass().getCanonicalName();
            CraftTweakerAPI.logError(message, loggerClassName);
        }
    }
    
    /**
     * Starts the scriptRun
     *
     * @throws Exception Any exception that occurs while running the script file.
     */
    public void run() throws Exception {
        if(!scriptLoadingOptions.isExecute()) {
            CraftTweakerAPI.logInfo("This is only a syntax check. Script changes will not be applied.");
        }
        
        if(isFirstRun()) {
            CraftTweakerAPI.logDebug("This is a first run. All IActions will be applied.");
        }
        
        initializeBep();
        registerModules();
        readAndExecuteScripts();
        
    }
    
    private void initializeBep() {
        this.bep = new IgnorePrefixCasingBracketParser();
        MinecraftForge.EVENT_BUS.post(new CTRegisterBEPEvent(bep));
    }
    
    private void readAndExecuteScripts() throws ParseException {
        SemanticModule scripts = scriptingEngine.createScriptedModule("scripts", sourceFiles, bep, FunctionParameter.NONE);
        
        if(!scripts.isValid()) {
            CraftTweakerAPI.logger.error("Scripts are invalid!");
            CraftTweaker.LOG.info("Scripts are invalid!");
            return;
        }
        
        //  toggle this to format scripts. Set by /ct format
        if(scriptLoadingOptions.isFormat()) {
            writeFormattedFiles(scripts);
        }
        
        if(scriptLoadingOptions.isExecute()) {
            //Now that we execute, we increment the runCount and therefore it's no longer a first run
            final LoaderActions loaderActions = getLoaderActions();
            CraftTweakerAPI.logDebug("This is loader '%s' run #%s", scriptLoadingOptions.getLoaderName(), loaderActions
                    .getRunCount() + 1);
            
            scriptingEngine.registerCompiled(scripts);
            scriptingEngine.run(Collections.emptyMap(), CraftTweaker.class.getClassLoader());
            loaderActions.incrementRunCount();
            
        } else if(CraftTweakerAPI.DEBUG_MODE) {
            scriptingEngine.createRunUnit().dump(new File("classes"));
        }
    }
    
    private void registerModules() throws CompileException {
        final List<JavaNativeModule> modules = new LinkedList<>();
        
        //Register crafttweaker module first to assign deps
        final JavaNativeModule crafttweakerModule = createModule(bep, CraftTweaker.MODID, CraftTweaker.MODID);
        
        scriptingEngine.registerNativeProvided(crafttweakerModule);
        modules.add(crafttweakerModule);
        
        final HashSet<String> rootPackages = new HashSet<>(CraftTweakerRegistry.getRootPackages());
        rootPackages.remove(CraftTweaker.MODID);
        for(String rootPackage : rootPackages) {
            final JavaNativeModule module = createModule(bep, rootPackage, rootPackage, crafttweakerModule);
            scriptingEngine.registerNativeProvided(module);
            modules.add(module);
        }
        
        
        final JavaNativeModule expModule = createModule(bep, "expansions", "", modules.toArray(new JavaNativeModule[0]));
        for(List<Class<?>> expansionList : CraftTweakerRegistry.getExpansions().values()) {
            for(Class<?> expansionClass : expansionList) {
                expModule.addClass(expansionClass);
            }
        }
        scriptingEngine.registerNativeProvided(expModule);

        final Pair<JavaNativeModule, CrTJavaNativeConverterBuilder> temp = createModuleAndConverterBuilder(bep, "native_strings", "");
        temp.getRight().getNativeExpansionConverter().addNativeMethodsToString();
        scriptingEngine.registerNativeProvided(temp.getLeft());
    }
    
    private JavaNativeModule createModule(IgnorePrefixCasingBracketParser bep, String moduleName, String basePackage, JavaNativeModule... dependencies) {
        return createModuleAndConverterBuilder(bep, moduleName, basePackage, dependencies).getLeft();
    }

    private Pair<JavaNativeModule, CrTJavaNativeConverterBuilder> createModuleAndConverterBuilder(IgnorePrefixCasingBracketParser bep, String moduleName, String basePackage, JavaNativeModule... dependencies) {
        CrTJavaNativeConverterBuilder javaNativeConverterBuilder = new CrTJavaNativeConverterBuilder();
        JavaNativeModule module = scriptingEngine.createNativeModule(moduleName, basePackage, dependencies, javaNativeConverterBuilder);

        for(ValidatedEscapableBracketParser bracketResolver : CraftTweakerRegistry.getBracketResolvers(moduleName, scriptingEngine, module)) {
            bep.register(bracketResolver.getName(), bracketResolver);
        }
        module.registerBEP(bep);
        for(Class<?> aClass : CraftTweakerRegistry.getGlobalsInPackage(moduleName)) {
            module.addGlobals(aClass);
        }
        for(Class<?> aClass : CraftTweakerRegistry.getClassesInPackage(moduleName)) {
            module.addClass(aClass);
        }

        return Pair.of(module, javaNativeConverterBuilder);
    }
    
    private void writeFormattedFiles(SemanticModule scripts) {
        List<HighLevelDefinition> all = scripts.definitions.getAll();
        ScriptFormattingSettings.Builder builder = new ScriptFormattingSettings.Builder();
        FileFormatter formatter = new FileFormatter(builder.build());
        List<ScriptBlock> blocks = scripts.scripts;
        for(ScriptBlock block : blocks) {
            String format = formatter.format(scripts.rootPackage, block, all);
            File parent = new File("scriptsFormatted");
            File file = new File(parent, block.file.getFilename());
            
            if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                CraftTweakerAPI.logError("Could not find or create folder %s, aborting formatting task!", file
                        .getParent());
            }
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(format);
            } catch(IOException e) {
                CraftTweakerAPI.logThrowing("Could not write formatted files", e);
            }
        }
    }
}
