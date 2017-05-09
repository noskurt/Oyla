package com.ygznsl.noskurt.oyla;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreatePollActivity extends AppCompatActivity {

    private EditText etxtQuestionCreate;
    private Button btnAddCreate;
    private Button btnSubmitCreate;
    private LinearLayout llOptionsCreate;

    private List<EditText> optionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);

        etxtQuestionCreate = (EditText) findViewById(R.id.etxtQuestionCreate);
        btnAddCreate = (Button) findViewById(R.id.btnAddCreate);
        btnSubmitCreate = (Button) findViewById(R.id.btnSubmitCreate);
        llOptionsCreate = (LinearLayout) findViewById(R.id.llOptionsCreate);

        optionsList = new ArrayList<>();

        btnAddCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout node = new LinearLayout(CreatePollActivity.this);
                node.setOrientation(LinearLayout.HORIZONTAL);
                node.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final EditText answer = new EditText(CreatePollActivity.this);
                answer.setHint("Cevap "+(optionsList.size()+1));
                answer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.15f));

                Button deleteBtn = new Button(CreatePollActivity.this);
                deleteBtn.setText("SİL");
                deleteBtn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.85f));

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        optionsList.remove(answer);
                        llOptionsCreate.removeView(node);
                    }
                });

                node.addView(answer);
                node.addView(deleteBtn);

                optionsList.add(answer);
                llOptionsCreate.addView(node);
            }
        });

        btnSubmitCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
