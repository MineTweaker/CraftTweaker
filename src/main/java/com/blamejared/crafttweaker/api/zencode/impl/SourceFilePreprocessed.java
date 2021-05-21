package com.blamejared.crafttweaker.api.zencode.impl;

import com.blamejared.crafttweaker.api.zencode.IPreprocessor;
import com.blamejared.crafttweaker.api.zencode.PreprocessorMatch;
import org.openzen.zencode.shared.SourceFile;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SourceFilePreprocessed implements SourceFile {
    
    private final String fileName;
    private List<String> fileContent;
    private final Map<IPreprocessor, List<PreprocessorMatch>> matches;
    
    public SourceFilePreprocessed(String fileName, List<String> fileContent, Map<IPreprocessor, List<PreprocessorMatch>> matches) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.matches = matches;
    }
    
    @Override
    public String getFilename() {
        return fileName;
    }
    
    @Override
    public Reader open() {
        return new StringReader(String.join(System.lineSeparator(), fileContent) + System.lineSeparator());
    }
    
    @Override
    public void update(String content) {
        fileContent = Arrays.asList(content.split(System.lineSeparator()));
    }
    
    public Map<IPreprocessor, List<PreprocessorMatch>> getMatches() {
    
        return matches;
    }
    
}
