/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.github.davidji80.contentresolver.model;


public abstract class MediaInfo {
    public int id;
    public String filePath;
    public long addTime;
    public String mimeType;
    public String title ;


    @Override
    public boolean equals(Object o) {
        if(o instanceof MediaInfo){
            MediaInfo info = (MediaInfo)o;
            return id == info.id;
        }
        return false;
    }
}
