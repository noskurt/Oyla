package com.ygznsl.noskurt.oyla;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.ygznsl.noskurt.oyla.entity.Category;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;
import com.ygznsl.noskurt.oyla.helper.Predicate;

import java.text.Collator;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

public class CreatePollActivity extends AppCompatActivity {

    private final List<EditText> pollOptions = Collections.synchronizedList(new LinkedList<EditText>());
    private final Locale locale = new Locale("tr", "TR");
    private boolean guiInitialized = false;
    private List<Category> categories;
    private int maxOptionId;
    private int maxPollId;
    private User user;

    private ProgressBar pbPollCreate;
    private EditText txtPollTitleCreate;
    private Spinner spinnerPollCategoryCreate;
    private Button btnAddPollOptionCreate;
    private Button btnCreatePollCreate;
    private RadioButton rdPollGenderMaleCreate;
    private RadioButton rdPollGenderFemaleCreate;
    private RadioButton rdPollGenderBothCreate;
    private CheckBox checkboxPollMultipleCreate;
    private TextInputLayout tilPollTitleCreate;
    private LinearLayout llOptionsCreate;
    private RadioGroup radioGroupPollGenderCreate;
    private ScrollView mainLayoutCreate;

    private void initializeGui(final OylaDatabase oyla) {
        categories = Category.getCategories(this).get();
        Collections.sort(categories, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                final Collator collator = Collator.getInstance(locale);
                return collator.compare(c1.getName(), c2.getName());
            }
        });

        user = (User) getIntent().getExtras().getSerializable("user");

        maxPollId = Entity.maxId(Entity.map(oyla.getPolls(), new Function<Poll, Integer>() {
            @Override
            public Integer apply(Poll in) {
                return in.getId();
            }
        }));

        maxOptionId = Entity.maxId(Entity.map(oyla.getOptions(), new Function<Option, Integer>() {
            @Override
            public Integer apply(Option in) {
                return in.getId();
            }
        }));

        pbPollCreate = (ProgressBar) findViewById(R.id.pbPollCreate);
        txtPollTitleCreate = (EditText) findViewById(R.id.txtPollTitleCreate);
        spinnerPollCategoryCreate = (Spinner) findViewById(R.id.spinnerPollCategoryCreate);
        rdPollGenderMaleCreate = (RadioButton) findViewById(R.id.rdPollGenderMaleCreate);
        rdPollGenderFemaleCreate = (RadioButton) findViewById(R.id.rdPollGenderFemaleCreate);
        rdPollGenderBothCreate = (RadioButton) findViewById(R.id.rdPollGenderBothCreate);
        checkboxPollMultipleCreate = (CheckBox) findViewById(R.id.checkboxPollMultipleCreate);
        btnAddPollOptionCreate = (Button) findViewById(R.id.btnAddPollOptionCreate);
        btnCreatePollCreate = (Button) findViewById(R.id.btnCreatePollCreate);
        tilPollTitleCreate = (TextInputLayout) findViewById(R.id.tilPollTitleCreate);
        llOptionsCreate = (LinearLayout) findViewById(R.id.llPollOptionsCreate);
        radioGroupPollGenderCreate = (RadioGroup) findViewById(R.id.radioGroupPollGenderCreate);
        mainLayoutCreate = (ScrollView) findViewById(R.id.mainLayoutCreate);

        spinnerPollCategoryCreate.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories));

        rdPollGenderMaleCreate.setTag("E");
        rdPollGenderFemaleCreate.setTag("K");
        rdPollGenderBothCreate.setTag("B");
        rdPollGenderBothCreate.setChecked(true);

        btnAddPollOptionCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout node = new LinearLayout(CreatePollActivity.this);
                node.setOrientation(LinearLayout.HORIZONTAL);
                node.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final EditText answer = new EditText(CreatePollActivity.this);
                answer.setHint("Anket Seçeneği");
                answer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.15f));

                final Button btnDeleteOption = new Button(CreatePollActivity.this);
                btnDeleteOption.setBackground(getDrawable(R.drawable.btn_colored1));
                btnDeleteOption.setTextColor(Color.parseColor("#FFF0A5"));
                btnDeleteOption.setText(getString(R.string.text_btnDeletePollOptionCreate));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0.85f);
                params.setMargins(5, 5, 5, 5);
                btnDeleteOption.setLayoutParams(params);

                btnDeleteOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pollOptions.remove(answer);
                        llOptionsCreate.removeView(node);
                    }
                });

                node.addView(answer);
                node.addView(btnDeleteOption);

                pollOptions.add(answer);
                llOptionsCreate.addView(node);
            }
        });

        btnCreatePollCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtPollTitleCreate.getText().toString().trim().length() == 0) {
                    tilPollTitleCreate.setErrorEnabled(true);
                    tilPollTitleCreate.setError("Anket sorusunu boş bırakamazsınız!");
                    return;
                }
                tilPollTitleCreate.setErrorEnabled(false);
                if (spinnerPollCategoryCreate.getSelectedItem() == null) {
                    Toast.makeText(CreatePollActivity.this, "Anketin kategorisini belirlemelisiniz!", Toast.LENGTH_LONG).show();
                    spinnerPollCategoryCreate.requestFocus();
                    return;
                }
                if (radioGroupPollGenderCreate.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(CreatePollActivity.this, "Ankete oy verebilecek cinsiyeti belirlemelisiniz!", Toast.LENGTH_LONG).show();
                    radioGroupPollGenderCreate.requestFocus();
                    return;
                }
                final List<String> nonEmptyOptions = Entity.findAllMatches(pollOptions, new Predicate<EditText>() {
                    @Override
                    public boolean test(EditText in) {
                        return in.getText().toString().trim().length() > 0;
                    }
                }, new Function<EditText, String>() {
                    @Override
                    public String apply(EditText in) {
                        return in.getText().toString();
                    }
                });
                if (nonEmptyOptions.size() < 2) {
                    Toast.makeText(CreatePollActivity.this, "Anketin en az 2 seçeneği olmalıdır!", Toast.LENGTH_LONG).show();
                    return;
                }

                final AlertDialog dialog = new AlertDialog.Builder(CreatePollActivity.this)
                        .setTitle("Anket oluşturulacak")
                        .setMessage("Emin misiniz?")
                        .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                pbPollCreate.setVisibility(View.VISIBLE);
                                mainLayoutCreate.setVisibility(View.GONE);

                                String genders = "B";
                                switch (radioGroupPollGenderCreate.getCheckedRadioButtonId()) {
                                    case R.id.rdPollGenderMaleCreate:
                                        genders = rdPollGenderMaleCreate.getTag().toString();
                                        break;
                                    case R.id.rdPollGenderFemaleCreate:
                                        genders = rdPollGenderFemaleCreate.getTag().toString();
                                        break;
                                    case R.id.rdPollGenderBothCreate:
                                        genders = rdPollGenderBothCreate.getTag().toString();
                                        break;
                                }

                                final int multiple = checkboxPollMultipleCreate.isChecked() ? 1 : 0;

                                final Poll poll = new Poll();
                                poll.setMult(multiple);
                                poll.setId(++maxPollId);
                                poll.setGenders(genders);
                                poll.setUser(user.getId());
                                poll.setUrl(oyla.randomPollUrl());
                                poll.setTitle(txtPollTitleCreate.getText().toString());
                                poll.setPdate(Poll.DATE_FORMAT.format(Calendar.getInstance(locale).getTime()));
                                poll.setCategory(((Category) spinnerPollCategoryCreate.getSelectedItem()).getId());

                                final List<Option> options = new LinkedList<>();
                                for (String o : nonEmptyOptions) {
                                    final Option option = new Option();
                                    option.setTitle(o);
                                    option.setPoll(maxPollId);
                                    option.setId(++maxOptionId);
                                    options.add(option);
                                }

                                final CountDownLatch latch = new CountDownLatch(options.size());

                                final DatabaseReference pushed = Entity.getDatabase().getReference().child("poll").push();
                                pushed.setValue(poll).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            oyla.addPoll(poll);
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        for (final Option option : options) {
                                                            final DatabaseReference pushedOption = Entity.getDatabase().getReference().child("option").push();
                                                            pushedOption.setValue(option).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    latch.countDown();
                                                                    if (task.isSuccessful()) {
                                                                        oyla.addOption(option);
                                                                    } else {
                                                                        Log.e("option.push", task.getException() == null ? "Error" : task.getException().getMessage());
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        latch.await();
                                                    } catch (InterruptedException ex) {
                                                        Log.e("options.push", ex.getMessage());
                                                    } finally {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                pbPollCreate.setVisibility(View.GONE);
                                                                mainLayoutCreate.setVisibility(View.VISIBLE);

                                                                final Intent intent = new Intent();
                                                                intent.putExtra("newPoll", poll);
                                                                CreatePollActivity.this.setResult(Activity.RESULT_OK, intent);
                                                                CreatePollActivity.this.finish();
                                                            }
                                                        });
                                                    }

                                                }
                                            }).start();
                                        } else {
                                            Toast.makeText(CreatePollActivity.this,
                                                    ("Anket oluştururken hata meydana geldi: \r\n" + (task.getException() == null ? "" : task.getException().getMessage())).trim(),
                                                    Toast.LENGTH_LONG).show();

                                            mainLayoutCreate.setVisibility(View.VISIBLE);
                                            pbPollCreate.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_poll);
        MyApplication.setIconBar(this);
        setTitle(" Anket Oluştur");
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}