package com.tistory.puzzleleaf.mvpsample.main;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.tistory.puzzleleaf.mvpsample.main.view.recycler.NotesViewHolder;
import com.tistory.puzzleleaf.mvpsample.models.Note;

/**
 * Created by cmtyx on 2017-10-02.
 */

public interface MVP_Main {

    //Presenter에서 필요한 View에서 제공되는 작업들
    interface RequiredViewOps{
        Context getAppContext();
        Context getActivityContext();
        void showToast(Toast toast);
        void showProgress();
        void hideProgress();
        void showAlert(AlertDialog dialog);
        void notifyItemInserted(int layoutPosition);
        void notifyItemRangeChanged(int positionStart, int itemCount);
        void notifyItemRemoved(int position);
        void notifyDataSetChanged();
        void clearEditText();
    }

    //View와 통신하기 위해서 Presenter에서 제공되는 작업
    interface ProvidedPresenterOps{
        int getNoteCount();
        NotesViewHolder createViewHolder(ViewGroup parent, int viewType);
        void bindViewHolder(NotesViewHolder holder, int position);
        void clickNewNote(EditText editText);
        void clickDeleteNote(Note note, int adapterPos, int layoutPos);
        void setView(RequiredViewOps view);
        void onDestroy(boolean isChangingConfiguration);

    }

    //Model에 필요한 Presenter에서 제공되는 작업
    interface RequiredPresenterOps{
        Context getAppContext();
        Context getActivityContext();
    }

    //Presenter와 통신을 위해 Model에서 제공하는 작업
    interface ProvidedModelOps{
        void onDestroy(boolean isChangingConfiguration);
        int insertNote(Note note);
        int getNotesCount();
        boolean loadData();
        boolean deleteNote(Note note, int adapterPos);
        Note getNote(int position);

    }
}
