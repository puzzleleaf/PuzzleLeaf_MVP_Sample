package com.tistory.puzzleleaf.mvpsample.main.view;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tistory.puzzleleaf.mvpsample.R;
import com.tistory.puzzleleaf.mvpsample.common.StateMaintainer;
import com.tistory.puzzleleaf.mvpsample.main.MVP_Main;
import com.tistory.puzzleleaf.mvpsample.main.model.MainModel;
import com.tistory.puzzleleaf.mvpsample.main.presenter.MainPresenter;
import com.tistory.puzzleleaf.mvpsample.main.view.recycler.NotesViewHolder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MVP_Main.RequiredViewOps{

    private EditText mTextNewNote;
    private ProgressBar mProgress;
    private ListNotes mListAdapter;

    //@TODO 생명주기와 관련된 클래스 (더 공부해 볼 것!)
    private final StateMaintainer mStateMaintainer =
            new StateMaintainer( getFragmentManager(), MainActivity.class.getName());

    private MVP_Main.ProvidedPresenterOps mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupViews();
        setupMVP();

    }

    private void setupViews(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(this);

        mTextNewNote = (EditText)findViewById(R.id.edit_note);
        mListAdapter = new ListNotes();
        mProgress = (ProgressBar)findViewById(R.id.progressbar);

        RecyclerView mList = (RecyclerView)findViewById(R.id.list_notes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mList.setLayoutManager(linearLayoutManager);
        mList.setAdapter(mListAdapter);
        mList.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupMVP(){
        if(mStateMaintainer.firstTimeIn()) {
            MainPresenter presenter = new MainPresenter(this);
            MainModel model = new MainModel(presenter);
            presenter.setModel(model);
            mStateMaintainer.put(presenter);
            mStateMaintainer.put(model);

            mPresenter = presenter;
        }
        else{
            mPresenter = mStateMaintainer.get(MainPresenter.class.getName());
            mPresenter.setView(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy(isChangingConfigurations());
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public void showToast(Toast toast) {
        toast.show();
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showAlert(AlertDialog dialog) {
        dialog.show();
    }

    @Override
    public void notifyItemInserted(int layoutPosition) {
        mListAdapter.notifyItemInserted(layoutPosition);
    }

    @Override
    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        mListAdapter.notifyItemRangeChanged(positionStart,itemCount);
    }

    @Override
    public void notifyItemRemoved(int position) {
        mListAdapter.notifyItemRemoved(position);
    }

    @Override
    public void notifyDataSetChanged() {
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearEditText() {
        mTextNewNote.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab:
                mPresenter.clickNewNote(mTextNewNote);
        }
    }

    private class ListNotes extends RecyclerView.Adapter<NotesViewHolder>{

        @Override
        public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mPresenter.createViewHolder(parent,viewType);
        }

        @Override
        public void onBindViewHolder(NotesViewHolder holder, int position) {
            mPresenter.bindViewHolder(holder,position);
        }

        @Override
        public int getItemCount() {
            return mPresenter.getNoteCount();
        }
    }
}
