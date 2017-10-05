package com.tistory.puzzleleaf.mvpsample.models;

import android.content.ContentValues;

import com.tistory.puzzleleaf.mvpsample.data.DBSchema;

/**
 * Created by cmtyx on 2017-10-02.
 */

public class Note {

    private int id = -1;
    private String mText;
    private String mDate;

    public Note(){

    }

    public Note(int id, String mText, String mDate){
        this.id = id;
        this.mText = mText;
        this.mDate = mDate;
    }

    //모르는 영역
    public ContentValues getValues(){
        ContentValues cv = new ContentValues();
        if(id != -1){
            cv.put(DBSchema.TB_NOTES.ID,id);
        }
        cv.put(DBSchema.TB_NOTES.NOTE,mText);
        cv.put(DBSchema.TB_NOTES.DATE,mDate);
        return cv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }
}
