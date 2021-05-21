package com.blamejared.crafttweaker.api.zencode.impl;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.ScriptLoadingOptions;
import com.blamejared.crafttweaker.api.zencode.IPreprocessor;
import com.blamejared.crafttweaker.api.zencode.PreprocessorMatch;
import org.openzen.zencode.shared.SourceFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;

public class FileAccessSingle {

    private final String fileName;
    private final Map<IPreprocessor, List<PreprocessorMatch>> matches = new HashMap<>();
    private final List<String> fileContents;
    private final Map<String, IPreprocessor> registeredPreprocessors;
    private final ScriptLoadingOptions scriptLoadingOptions;
    private boolean shouldBeLoaded;

    /**
     * Constructs a new FileAccessSingle object
     *
     * <p>The file should be accessible, if an IOException occurs it will be logged and the content will remain empty</p>
     */
    public FileAccessSingle(File file, ScriptLoadingOptions scriptLoadingOptions, Collection<IPreprocessor> preprocessors) {
        this.scriptLoadingOptions = scriptLoadingOptions;
        this.registeredPreprocessors = new HashMap<>();
        for (IPreprocessor preprocessor : preprocessors) {
            this.registeredPreprocessors.put(preprocessor.getName().toLowerCase(Locale.ENGLISH), preprocessor);
        }
        this.fileName = file.getName();
        this.fileContents = new ArrayList<>();
        try {
            readFile(new FileReader(file));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        fillInMissingPreprocessors();
        applyPreprocessors();
    }

    /**
     * Constructs a new FileAccessSingle object
     *
     * <p>The file should be accessible, if an IOException occurs it will be logged and the content will remain empty</p>
     * Provides the base scripts directory, file names will be resolved against that path.
     *
     * @throws IllegalArgumentException baseDirectory is no parent of file
     */
    public FileAccessSingle(File baseDirectory, File file, ScriptLoadingOptions scriptLoadingOptions, Collection<IPreprocessor> preprocessors) {
        this.scriptLoadingOptions = scriptLoadingOptions;
        if(!file.getAbsolutePath().startsWith(baseDirectory.getAbsolutePath())) {
            throw new IllegalArgumentException("Base directory is not parent of script file!");
        }

        this.registeredPreprocessors = new HashMap<>();
        for (IPreprocessor preprocessor : preprocessors) {
            this.registeredPreprocessors.put(preprocessor.getName().toLowerCase(Locale.ENGLISH), preprocessor);
        }
        this.fileName = file.getAbsolutePath().substring(baseDirectory.getAbsolutePath().length() + 1);
        this.fileContents = new ArrayList<>();
        try {
            readFile(new FileReader(file));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        fillInMissingPreprocessors();
        applyPreprocessors();
    }
    
    
    /**
     * Constructs a new FileAccessSingle object from a string
     *
     */
    public FileAccessSingle(String fileName, Reader reader, ScriptLoadingOptions scriptLoadingOptions, Collection<IPreprocessor> preprocessors) {
        this.scriptLoadingOptions = scriptLoadingOptions;

        this.registeredPreprocessors = new HashMap<>();
        for (IPreprocessor preprocessor : preprocessors) {
            this.registeredPreprocessors.put(preprocessor.getName().toLowerCase(Locale.ENGLISH), preprocessor);
        }

        this.fileName = fileName;
        this.fileContents = new ArrayList<>();
        readFile(reader);
        fillInMissingPreprocessors();
        applyPreprocessors();
    }
    
    public static Comparator<FileAccessSingle> createComparator(Collection<IPreprocessor> preprocessors) {
        List<IPreprocessor> list = new ArrayList<>(preprocessors);
        list.sort(Comparator.comparingInt(IPreprocessor::getPriority).reversed());

        Comparator<FileAccessSingle> out = null;
        for (IPreprocessor pp : list) {
            out = out == null ? pp : out.thenComparing(pp);
        }
        return out == null ? Comparator.comparing(FileAccessSingle::getFileName) : out.thenComparing(FileAccessSingle::getFileName);
    }

    private void fillInMissingPreprocessors() {
        registeredPreprocessors.forEach((key, prep) -> {
            final String defaultValue = prep.getDefaultValue();

            if (defaultValue != null && !matches.containsKey(prep)) {
                matches.put(prep, Collections.singletonList(new PreprocessorMatch(prep, -1, defaultValue)));
            }
        });
    }

    private void applyPreprocessors() {
        final ArrayList<Map.Entry<IPreprocessor, List<PreprocessorMatch>>> entries = new ArrayList<>(matches.entrySet());
        entries.sort(Comparator.comparingInt((Map.Entry<IPreprocessor, ?> e) -> e.getKey().getPriority()).reversed());

        for (Map.Entry<IPreprocessor, List<PreprocessorMatch>> entry : entries) {
            shouldBeLoaded = entry.getKey().apply(this, scriptLoadingOptions, entry.getValue());
            if (!shouldBeLoaded)
                return;
        }
    }

    private void readFile(Reader reader) {
        try (final BufferedReader bufferedReader = new BufferedReader(reader)) {
            int i = 0;
            String line;
            if (bufferedReader.ready()) {
                while((line =  bufferedReader.readLine()) !=null) {
                    this.checkPreprocessor(line, ++i);
                    this.fileContents.add(line);
                }
            }
        } catch (IOException e) {
            CraftTweakerAPI.logThrowing("Could not load file %s", e, this.fileName);
        }
    }

    private void checkPreprocessor(String line, int lineNumber) {
        final Matcher matcher = IPreprocessor.preprocessorPattern.matcher(line);
        if (!matcher.find())
            return;

        final String g = matcher.group();
        final String preprocessorName = g.substring(1).trim().toLowerCase(Locale.ENGLISH);
        if (!registeredPreprocessors.containsKey(preprocessorName))
            return;

        final IPreprocessor preprocessor = registeredPreprocessors.get(preprocessorName);
        final List<PreprocessorMatch> matches = this.matches.computeIfAbsent(preprocessor, p -> new ArrayList<>(1));
        matches.add(new PreprocessorMatch(preprocessor, lineNumber, line.substring(matcher.end())));
    }

    public Map<IPreprocessor, List<PreprocessorMatch>> getMatches() {
        return matches;
    }

    public List<PreprocessorMatch> getMatchesFor(IPreprocessor preprocessor) {
        return matches.get(preprocessor);
    }

    public boolean hasMatchFor(IPreprocessor preprocessor) {
        return matches.containsKey(preprocessor);
    }

    public List<String> getFileContents() {
        return fileContents;
    }

    public SourceFile getSourceFile() {
        return new SourceFilePreprocessed(fileName, fileContents, matches);
    }

    public boolean shouldBeLoaded() {
        return shouldBeLoaded;
    }

    public String getFileName() {
        return fileName;
    }

    public ScriptLoadingOptions getScriptLoadingOptions() {
        return scriptLoadingOptions;
    }
}
