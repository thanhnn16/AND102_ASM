package com.miwth.and102_asm.database;

public interface UploadCallBack {
    void onUploadComplete();

    void onUploadError(Throwable throwable);
}
