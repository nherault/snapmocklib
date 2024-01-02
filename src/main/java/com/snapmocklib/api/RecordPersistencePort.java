package com.snapmocklib.api;

import java.io.IOException;

public interface RecordPersistencePort {

    void saveRecord(String recordId, String recordData) throws IOException;
    String getRecord(String recordId) throws IOException;
}
