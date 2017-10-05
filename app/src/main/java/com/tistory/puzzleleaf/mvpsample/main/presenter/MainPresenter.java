package com.tistory.puzzleleaf.mvpsample.main.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.tistory.puzzleleaf.mvpsample.R;
import com.tistory.puzzleleaf.mvpsample.main.MVP_Main;
import com.tistory.puzzleleaf.mvpsample.main.view.recycler.NotesViewHolder;
import com.tistory.puzzleleaf.mvpsample.models.Note;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cmtyx on 2017-10-02.
 */

public class MainPresenter implements MVP_Main.ProvidedPresenterOps, MVP_Main.RequiredPresenterOps{

    //WeakReference - GC가 발생되기 전까지는 참조를 유지하고 GC가 발생하면 무조건 회수된다는 점에서 차이가 있습니다.
    // 이는 유용하게 쓰일 수 있는데, 짧은 시간, 자주 쓰일 수 있는 객체를 이용할 때 유용하게 사용될 수 있다.
    private WeakReference<MVP_Main.RequiredViewOps> mView;
    private MVP_Main.ProvidedModelOps mModel;

    public MainPresenter(MVP_Main.RequiredViewOps view){
        mView = new WeakReference<MVP_Main.RequiredViewOps>(view);
    }

    private MVP_Main.RequiredViewOps getView() throws NullPointerException{
        if(mView !=null){
            return  mView.get(); //WeakReference는 .get() 메서드로 객체를 얻을 수 있다.
        }else{
            throw new NullPointerException("View is unavailable");
        }
    }

    public void setModel(MVP_Main.ProvidedModelOps model){
        mModel = model;
        loadData();
    }

    private void loadData(){
        try{
            getView().showProgress();
            new AsyncTask<Void,Void,Boolean>(){

                @Override
                protected Boolean doInBackground(Void... voids) {
                    return mModel.loadData();
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    try {
                        getView().hideProgress();
                        if(!result){
                            getView().showToast(makeToast("Error Loading"));
                        }else{
                            getView().notifyDataSetChanged();
                        }
                    }catch (NullPointerException e){

                    }
                }
            }.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Toast makeToast(String msg){
        return Toast.makeText(getView().getAppContext(),msg,Toast.LENGTH_SHORT);
    }

    public Note makeNote(String noteText){
        Note note = new Note();
        note.setText(noteText);
        note.setDate(getDate());
        return note;
    }

    public String getDate(){
        return new SimpleDateFormat("HH:mm:ss - MM/dd/yyyy", Locale.getDefault()).format(new Date());
    }

    private void openDeleteAlert(final Note note, final int adapterPos, final int layoutPos){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivityContext());
        alertBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteNote(note,adapterPos,layoutPos);
            }
        });

        alertBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });

        alertBuilder.setTitle("Delete Note");
        alertBuilder.setMessage("Delete " + note.getText() +"?");

        AlertDialog alertDialog = alertBuilder.create();
        try {
            getView().showAlert(alertDialog);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void deleteNote(final Note note, final int adapterPos, final int layoutPos){
        getView().showProgress();
        new AsyncTask<Void, Void, Boolean>(){
            @Override
            protected Boolean doInBackground(Void... voids) {
                return mModel.deleteNote(note,adapterPos);
            }

            @Override
            protected void onPostExecute(Boolean result) {
               try {
                   getView().hideProgress();
                   if (result){
                       getView().notifyItemRemoved(layoutPos);
                       getView().showToast(makeToast("Note deleted."));
                   }else{
                       getView().showToast(makeToast("Error deleting note[" + note.getId() + "]"));
                   }
               }catch (NullPointerException e){
                    e.printStackTrace();
               }
            }
        }.execute();
    }


    @Override
    public int getNoteCount() {
        return mModel.getNotesCount();
    }

    @Override
    public NotesViewHolder createViewHolder(ViewGroup parent, int viewType) {
        NotesViewHolder viewHolder;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View viewTaskRow = layoutInflater.inflate(R.layout.holder_notes,parent,false);
        viewHolder = new NotesViewHolder(viewTaskRow);

        return viewHolder;
    }

    @Override
    public void bindViewHolder(final NotesViewHolder holder, int position) {
        final Note note = mModel.getNote(position);
        holder.text.setText(note.getText());
        holder.date.setText(note.getDate());
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickDeleteNote(note, holder.getAdapterPosition(),holder.getLayoutPosition());
            }
        });
    }

    @Override
    public void clickNewNote(EditText editText) {
        getView().showProgress();
        final String noteText = editText.getText().toString();
        if(!noteText.isEmpty()){
            new AsyncTask<Void,Void,Integer>(){
                @Override
                protected Integer doInBackground(Void... voids) {
                    return mModel.insertNote(makeNote(noteText));
                }

                @Override
                protected void onPostExecute(Integer adapterPosition) {
                    try {
                        if(adapterPosition>-1){
                            getView().clearEditText();
                            getView().notifyItemInserted(adapterPosition+1);
                            getView().notifyItemRangeChanged(adapterPosition,mModel.getNotesCount());
                        }else{
                            getView().hideProgress();
                            getView().showToast(makeToast("Error creating note [" + noteText +"]"));
                        }
                    }catch (NullPointerException e){

                    }
                }
            }.execute();
        }
    }

    @Override
    public void clickDeleteNote(Note note, int adapterPos, int layoutPos) {
        openDeleteAlert(note, adapterPos, layoutPos);
    }

    @Override
    public void setView(MVP_Main.RequiredViewOps view) {
        mView = new WeakReference<MVP_Main.RequiredViewOps>(view);
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        mView = null;
        mModel.onDestroy(isChangingConfiguration);
        if(!isChangingConfiguration){
            mModel = null;
        }

    }

    @Override
    public Context getAppContext() {
        try {
            return getView().getAppContext();
        }catch (NullPointerException e){
            return null;
        }
    }

    @Override
    public Context getActivityContext() {
        try {
            return getView().getActivityContext();
        }catch (NullPointerException e){
            return null;
        }
    }
}
