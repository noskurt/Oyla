package com.ygznsl.noskurt.oyla;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ygznsl.noskurt.oyla.entity.City;
import com.ygznsl.noskurt.oyla.entity.Entity;
import com.ygznsl.noskurt.oyla.entity.Option;
import com.ygznsl.noskurt.oyla.entity.Poll;
import com.ygznsl.noskurt.oyla.entity.User;
import com.ygznsl.noskurt.oyla.entity.Vote;
import com.ygznsl.noskurt.oyla.helper.Function;
import com.ygznsl.noskurt.oyla.helper.Nullable;
import com.ygznsl.noskurt.oyla.helper.OylaDatabase;
import com.ygznsl.noskurt.oyla.helper.RadioButtonCollection;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import java.text.Collator;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PollAnalyticsActivity extends AppCompatActivity {

    private final Locale locale = new Locale("tr", "TR");
    private boolean guiInitialized = false;
    private boolean anonymous;
    private Poll poll;
    private List<Vote> votes;
    private List<City> cities;
    private List<Option> options;

    private LinearLayout relOptionsPollAnalytics;
    private ImageView btnLeftPollAnalytics;
    private ImageView btnRightPollAnalytics;
    private TextView txtDetail1PollAnalytics;
    private TextView txtDetail2PollAnalytics;
    private TextView txtDetail3PollAnalytics;
    private TextView txtUserCannotSeeDetailedAnalysis;
    private Button btnDetailedAnalysisPollAnalytics;

    private View createAccordingToAllGender(OylaDatabase oyla){
        final List<Vote> forMale = new LinkedList<>();
        final List<Vote> forFemale = new LinkedList<>();
        for (Vote vote : votes){
            final User user = oyla.getUserById(vote.getU());
            if (user.getGender().equals("E")) forMale.add(vote);
            else forFemale.add(vote);
        }

        final LayoutInflater inflater = getLayoutInflater();

        final View viewForMale = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
        final View viewForFemale = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);

        final TextView lblForMale = (TextView) viewForMale.findViewById(R.id.lblVoteCountWithout);
        final TextView lblForFemale = (TextView) viewForFemale.findViewById(R.id.lblVoteCountWithout);

        final ProgressBar pbForMale = (ProgressBar) viewForMale.findViewById(R.id.pbVoteCountWithout);
        final ProgressBar pbForFemale = (ProgressBar) viewForFemale.findViewById(R.id.pbVoteCountWithout);

        final TextView txtForMale = (TextView) viewForMale.findViewById(R.id.txtVoteCountWithout);
        final TextView txtForFemale = (TextView) viewForFemale.findViewById(R.id.txtVoteCountWithout);

        final float percentageForMaleFloat = (((float) forMale.size()) / votes.size()) * 100;
        final int percentageForMale = Math.round(percentageForMaleFloat);

        final float percentageForFemaleFloat = (((float) forFemale.size()) / votes.size()) * 100;
        final int percentageForFemale = Math.round(percentageForFemaleFloat);

        lblForMale.setText(String.format(locale, "(%d) Erkek", forMale.size()));
        lblForFemale.setText(String.format(locale, "(%d) Kadın", forFemale.size()));

        pbForMale.setProgress(percentageForMale);
        pbForFemale.setProgress(percentageForFemale);

        txtForMale.setText(String.format(locale, "%.2f%%", percentageForMaleFloat));
        txtForFemale.setText(String.format(locale, "%.2f%%", percentageForFemaleFloat));

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);

        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(String.valueOf("Oylanan: Anket Geneli"));

        if (forFemale.size() < forMale.size()){
            analysisLayout.addView(viewForMale);
            analysisLayout.addView(viewForFemale);
        } else {
            analysisLayout.addView(viewForFemale);
            analysisLayout.addView(viewForMale);
        }

        return view;
    }

    private View createAccordingToGender(OylaDatabase oyla, int optionId){
        int totalSize = 0;
        final List<Vote> forMale = new LinkedList<>();
        final List<Vote> forFemale = new LinkedList<>();
        for (Vote vote : votes){
            if (vote.getO() != optionId) continue;
            final User user = oyla.getUserById(vote.getU());
            if (user.getGender().equals("E")) forMale.add(vote);
            else forFemale.add(vote);
            totalSize++;
        }

        final LayoutInflater inflater = getLayoutInflater();

        final View viewForMale = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
        final View viewForFemale = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);

        final TextView lblForMale = (TextView) viewForMale.findViewById(R.id.lblVoteCountWithout);
        final TextView lblForFemale = (TextView) viewForFemale.findViewById(R.id.lblVoteCountWithout);

        final ProgressBar pbForMale = (ProgressBar) viewForMale.findViewById(R.id.pbVoteCountWithout);
        final ProgressBar pbForFemale = (ProgressBar) viewForFemale.findViewById(R.id.pbVoteCountWithout);

        final TextView txtForMale = (TextView) viewForMale.findViewById(R.id.txtVoteCountWithout);
        final TextView txtForFemale = (TextView) viewForFemale.findViewById(R.id.txtVoteCountWithout);

        final float percentageForMaleFloat = (((float) forMale.size()) / totalSize) * 100;
        final int percentageForMale = Math.round(percentageForMaleFloat);

        final float percentageForFemaleFloat = (((float) forFemale.size()) / totalSize) * 100;
        final int percentageForFemale = Math.round(percentageForFemaleFloat);

        lblForMale.setText(String.format(locale, "(%d) Erkek", forMale.size()));
        lblForFemale.setText(String.format(locale, "(%d) Kadın", forFemale.size()));

        pbForMale.setProgress(percentageForMale);
        pbForFemale.setProgress(percentageForFemale);

        txtForMale.setText(String.format(locale, "%.2f%%", percentageForMaleFloat));
        txtForFemale.setText(String.format(locale, "%.2f%%", percentageForFemaleFloat));

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);

        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(new Nullable<>(oyla.getOptionById(optionId)).orElse(new Function<Option, String>() {
            @Override
            public String apply(Option in) {
                return String.format(locale, "Oylanan: %s", in.getTitle());
            }
        }, "Oylanan: "));

        if (forFemale.size() < forMale.size()){
            analysisLayout.addView(viewForMale);
            analysisLayout.addView(viewForFemale);
        } else {
            analysisLayout.addView(viewForFemale);
            analysisLayout.addView(viewForMale);
        }

        return view;
    }

    private View createAccordingToAllAge(OylaDatabase oyla){
        final HashMap<String, List<Vote>> voteMap = new HashMap<>();
        for (String ageInterval : Arrays.asList("18'den küçük", "18-25", "26-40", "41-65", "65'ten büyük")){
            voteMap.put(ageInterval, new LinkedList<Vote>());
        }
        for (Vote vote : votes){
            final User user = oyla.getUserById(vote.getU());
            try {
                final LocalDate now = LocalDate.now();
                final LocalDate birthDate = LocalDate.fromDateFields(User.DATE_FORMAT.parse(user.getBdate()));
                final int age = Years.yearsBetween(birthDate, now).getYears();
                if (age < 18){
                    voteMap.get("18'den küçük").add(vote);
                } else if (age >= 18 && age <= 25) {
                    voteMap.get("18-25").add(vote);
                } else if (age >= 26 && age <= 40) {
                    voteMap.get("26-40").add(vote);
                } else if (age >= 41 && age <= 65) {
                    voteMap.get("41-65").add(vote);
                } else {
                    voteMap.get("65'ten büyük").add(vote);
                }
            } catch (ParseException ex) {
                Log.e("createAccordingToAge", ex.getMessage());
            }
        }

        final LayoutInflater inflater = getLayoutInflater();

        final List<View> views = new LinkedList<>();
        for (Map.Entry<String, List<Vote>> entry : voteMap.entrySet()){
            final View view = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
            final TextView lblVoteCountWithout = (TextView) view.findViewById(R.id.lblVoteCountWithout);
            final ProgressBar pbVoteCountWithout = (ProgressBar) view.findViewById(R.id.pbVoteCountWithout);
            final TextView txtVoteCountWithout = (TextView) view.findViewById(R.id.txtVoteCountWithout);

            final float percentageFloat = (((float) entry.getValue().size()) / votes.size()) * 100;
            final int percentage = Math.round(percentageFloat);

            lblVoteCountWithout.setText(String.format(locale, "(%d) %s", entry.getValue().size(), entry.getKey()));
            pbVoteCountWithout.setProgress(percentage);
            txtVoteCountWithout.setText(String.format(locale, "%.2f%%", percentageFloat));

            view.setTag(percentageFloat);
            views.add(view);
        }

        Collections.sort(views, new Comparator<View>() {
            @Override
            public int compare(View v1, View v2) {
                return ((Float) v2.getTag()).compareTo((Float) v1.getTag());
            }
        });

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);
        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(String.valueOf("Oylanan: Anket Geneli"));

        for (View v : views) analysisLayout.addView(v);

        return view;
    }

    private View createAccordingToAge(OylaDatabase oyla, int optionId){
        int totalSize = 0;
        final HashMap<String, List<Vote>> voteMap = new HashMap<>();
        for (String ageInterval : Arrays.asList("18'den küçük", "18-25", "26-40", "41-65", "65'ten büyük")){
            voteMap.put(ageInterval, new LinkedList<Vote>());
        }
        for (Vote vote : votes){
            if (vote.getO() != optionId) continue;
            final User user = oyla.getUserById(vote.getU());
            try {
                final LocalDate now = LocalDate.now();
                final LocalDate birthDate = LocalDate.fromDateFields(User.DATE_FORMAT.parse(user.getBdate()));
                final int age = Years.yearsBetween(birthDate, now).getYears();
                if (age < 18){
                    voteMap.get("18'den küçük").add(vote);
                } else if (age >= 18 && age <= 25) {
                    voteMap.get("18-25").add(vote);
                } else if (age >= 26 && age <= 40) {
                    voteMap.get("26-40").add(vote);
                } else if (age >= 41 && age <= 65) {
                    voteMap.get("41-65").add(vote);
                } else {
                    voteMap.get("65'ten büyük").add(vote);
                }
            } catch (ParseException ex) {
                Log.e("createAccordingToAge", ex.getMessage());
            } finally {
                totalSize++;
            }
        }

        final LayoutInflater inflater = getLayoutInflater();

        final List<View> views = new LinkedList<>();
        for (Map.Entry<String, List<Vote>> entry : voteMap.entrySet()){
            final View view = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
            final TextView lblVoteCountWithout = (TextView) view.findViewById(R.id.lblVoteCountWithout);
            final ProgressBar pbVoteCountWithout = (ProgressBar) view.findViewById(R.id.pbVoteCountWithout);
            final TextView txtVoteCountWithout = (TextView) view.findViewById(R.id.txtVoteCountWithout);

            final float percentageFloat = (((float) entry.getValue().size()) / totalSize) * 100;
            final int percentage = Math.round(percentageFloat);

            lblVoteCountWithout.setText(String.format(locale, "(%d) %s", entry.getValue().size(), entry.getKey()));
            pbVoteCountWithout.setProgress(percentage);
            txtVoteCountWithout.setText(String.format(locale, "%.2f%%", percentageFloat));

            view.setTag(percentageFloat);
            views.add(view);
        }

        Collections.sort(views, new Comparator<View>() {
            @Override
            public int compare(View v1, View v2) {
                return ((Float) v2.getTag()).compareTo((Float) v1.getTag());
            }
        });

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);
        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(new Nullable<>(oyla.getOptionById(optionId)).orElse(new Function<Option, String>() {
            @Override
            public String apply(Option in) {
                return String.format(locale, "Oylanan: %s", in.getTitle());
            }
        }, "Oylanan: "));

        for (View v : views) analysisLayout.addView(v);

        return view;
    }

    private View createAccordingToAllCity(OylaDatabase oyla){
        final HashMap<City, List<Vote>> voteMap = new HashMap<>();
        for (Vote vote : votes){
            final User user = oyla.getUserById(vote.getU());
            final City city = Entity.findById(cities, user.getCity()).get();
            if (voteMap.containsKey(city)){
                voteMap.get(city).add(vote);
            } else {
                final List<Vote> list = new LinkedList<>();
                list.add(vote);
                voteMap.put(city, list);
            }
        }

        final LayoutInflater inflater = getLayoutInflater();
        final List<View> views = new LinkedList<>();
        for (Map.Entry<City, List<Vote>> entry : voteMap.entrySet()){
            final View view = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
            final TextView lblVoteCountWithout = (TextView) view.findViewById(R.id.lblVoteCountWithout);
            final ProgressBar pbVoteCountWithout = (ProgressBar) view.findViewById(R.id.pbVoteCountWithout);
            final TextView txtVoteCountWithout = (TextView) view.findViewById(R.id.txtVoteCountWithout);

            final float percentageFloat = (((float) entry.getValue().size()) / votes.size()) * 100;
            final int percentage = Math.round(percentageFloat);

            lblVoteCountWithout.setTag(entry.getKey().getName());
            lblVoteCountWithout.setText(String.format(locale, "(%d) %s", entry.getValue().size(), entry.getKey()));
            pbVoteCountWithout.setProgress(percentage);
            txtVoteCountWithout.setText(String.format(locale, "%.2f%%", percentageFloat));

            view.setTag(percentageFloat);
            views.add(view);
        }

        Collections.sort(views, new Comparator<View>() {
            @Override
            public int compare(View v1, View v2) {
                final int first = ((Float) v2.getTag()).compareTo((Float) v1.getTag());
                if (first != 0) return first;
                final Collator collator = Collator.getInstance(locale);
                final TextView txt1 = (TextView) v1.findViewById(R.id.lblVoteCountWithout);
                final TextView txt2 = (TextView) v2.findViewById(R.id.lblVoteCountWithout);
                final String city1 = txt1.getTag().toString();
                final String city2 = txt2.getTag().toString();
                return collator.compare(city1, city2);
            }
        });

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);
        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(String.valueOf("Oylanan: Anket Geneli"));

        for (View v : views) analysisLayout.addView(v);

        return view;
    }

    private View createAccordingToCity(OylaDatabase oyla, int optionId){
        int totalSize = 0;
        final HashMap<City, List<Vote>> voteMap = new HashMap<>();
        for (Vote vote : votes){
            if (vote.getO() != optionId) continue;
            final User user = oyla.getUserById(vote.getU());
            final City city = Entity.findById(cities, user.getCity()).get();
            if (voteMap.containsKey(city)){
                voteMap.get(city).add(vote);
            } else {
                final List<Vote> list = new LinkedList<>();
                list.add(vote);
                voteMap.put(city, list);
            }
            totalSize++;
        }

        final LayoutInflater inflater = getLayoutInflater();
        final List<View> views = new LinkedList<>();
        for (Map.Entry<City, List<Vote>> entry : voteMap.entrySet()){
            final View view = inflater.inflate(R.layout.vote_count_control_without_radio_button, null);
            final TextView lblVoteCountWithout = (TextView) view.findViewById(R.id.lblVoteCountWithout);
            final ProgressBar pbVoteCountWithout = (ProgressBar) view.findViewById(R.id.pbVoteCountWithout);
            final TextView txtVoteCountWithout = (TextView) view.findViewById(R.id.txtVoteCountWithout);

            final float percentageFloat = (((float) entry.getValue().size()) / totalSize) * 100;
            final int percentage = Math.round(percentageFloat);

            lblVoteCountWithout.setTag(entry.getKey().getName());
            lblVoteCountWithout.setText(String.format(locale, "(%d) %s", entry.getValue().size(), entry.getKey()));
            pbVoteCountWithout.setProgress(percentage);
            txtVoteCountWithout.setText(String.format(locale, "%.2f%%", percentageFloat));

            view.setTag(percentageFloat);
            views.add(view);
        }

        Collections.sort(views, new Comparator<View>() {
            @Override
            public int compare(View v1, View v2) {
                final int first = ((Float) v2.getTag()).compareTo((Float) v1.getTag());
                if (first != 0) return first;
                final Collator collator = Collator.getInstance(locale);
                final TextView txt1 = (TextView) v1.findViewById(R.id.lblVoteCountWithout);
                final TextView txt2 = (TextView) v2.findViewById(R.id.lblVoteCountWithout);
                final String city1 = txt1.getTag().toString();
                final String city2 = txt2.getTag().toString();
                return collator.compare(city1, city2);
            }
        });

        final View view = inflater.inflate(R.layout.detailed_analysis_dialog, null);
        final TextView txtOptionTitleDetailedAnalysisDialog = (TextView) view.findViewById(R.id.txtOptionTitleDetailedAnalysisDialog);
        final LinearLayout analysisLayout = (LinearLayout) view.findViewById(R.id.analysisLayout);

        txtOptionTitleDetailedAnalysisDialog.setText(new Nullable<>(oyla.getOptionById(optionId)).orElse(new Function<Option, String>() {
            @Override
            public String apply(Option in) {
                return String.format(locale, "Oylanan: %s", in.getTitle());
            }
        }, "Oylanan: "));

        for (View v : views) analysisLayout.addView(v);

        return view;
    }

    private void initializeGui(final OylaDatabase oyla){
        cities = City.getCities(this).get();

        final Bundle extras = getIntent().getExtras();
        anonymous = extras.getBoolean("anonymous");
        poll = (Poll) extras.getSerializable("poll");

        options = oyla.optionsOfPoll(poll);
        votes = oyla.votesOfPoll(poll);

        final HashMap<Integer, List<Vote>> voteCounts = new HashMap<>();
        for (Vote vote : votes){
            final List<Vote> list = voteCounts.get(vote.getO());
            if (list != null){
                list.add(vote);
            } else {
                final List<Vote> l = new LinkedList<>();
                l.add(vote);
                voteCounts.put(vote.getO(), l);
            }
        }

        for (Option option : options){
            if (!voteCounts.containsKey(option.getId())){
                voteCounts.put(option.getId(), new LinkedList<Vote>());
            }
        }

        final RadioButtonCollection radioGroup = new RadioButtonCollection();
        /*radioGroup.setOnSelectedItemChanged(new ValueChangedEvent<RadioButton>() {
            @Override
            public void valueChanged(RadioButton oldValue, RadioButton newValue) {
                if (!anonymous && btnDetailedAnalysisPollAnalytics != null){
                    btnDetailedAnalysisPollAnalytics.setEnabled(newValue != null);
                }
            }
        });*/

        final List<View> views = new LinkedList<>();
        final LayoutInflater inflater = getLayoutInflater();
        for (final Map.Entry<Integer, List<Vote>> entry : voteCounts.entrySet()){
            final View view = inflater.inflate(R.layout.vote_count_control, null);
            final RadioButton radioVoteCount = (RadioButton) view.findViewById(R.id.radioVoteCount);
            final ProgressBar pbVoteCount = (ProgressBar) view.findViewById(R.id.pbVoteCount);
            final TextView txtVoteCount = (TextView) view.findViewById(R.id.txtVoteCount);
            final float percentageFloat = (((float) entry.getValue().size()) / votes.size()) * 100;
            final int percentage = Math.round(percentageFloat);

            radioGroup.add(radioVoteCount);
            radioVoteCount.setTag(entry.getKey());
            radioVoteCount.setText(new Nullable<>(oyla.getOptionById(entry.getKey())).orElse(new Function<Option, String>() {
                @Override
                public String apply(Option in) {
                    return String.format(locale, "(%d) %s", entry.getValue().size(), in.getTitle());
                }
            }, ""));
            pbVoteCount.setProgress(percentage);
            txtVoteCount.setText(String.format(locale, "%.2f%%", percentageFloat));

            views.add(view);
        }

        Collections.sort(views, new Comparator<View>() {
            @Override
            public int compare(View v1, View v2) {
                final ProgressBar pb1 = (ProgressBar) v1.findViewById(R.id.pbVoteCount);
                final ProgressBar pb2 = (ProgressBar) v2.findViewById(R.id.pbVoteCount);
                return Integer.valueOf(pb2.getProgress()).compareTo(pb1.getProgress());
            }
        });

        relOptionsPollAnalytics = (LinearLayout) findViewById(R.id.relOptionsPollAnalytics);
        btnLeftPollAnalytics = (ImageView) findViewById(R.id.btnLeftPollAnalytics);
        btnRightPollAnalytics = (ImageView) findViewById(R.id.btnRightPollAnalytics);
        txtDetail1PollAnalytics = (TextView) findViewById(R.id.txtDetail1PollAnalytics);
        txtDetail2PollAnalytics = (TextView) findViewById(R.id.txtDetail2PollAnalytics);
        txtDetail3PollAnalytics = (TextView) findViewById(R.id.txtDetail3PollAnalytics);
        txtUserCannotSeeDetailedAnalysis = (TextView) findViewById(R.id.txtUserCannotSeeDetailedAnalysis);
        btnDetailedAnalysisPollAnalytics = (Button) findViewById(R.id.btnDetailedAnalysisPollAnalytics);

        for (View view : views) relOptionsPollAnalytics.addView(view);

        // TODO sağ ve sol oklarına gerek olmayabilir radio button atsak yeterli olabilir

        btnLeftPollAnalytics.setTag("false");
        btnRightPollAnalytics.setTag("true");

        btnLeftPollAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean enabled = Boolean.valueOf(btnLeftPollAnalytics.getTag().toString());
                if (enabled){
                    if (txtDetail3PollAnalytics.getVisibility() == View.VISIBLE) {
                        txtDetail3PollAnalytics.setVisibility(View.GONE);
                        txtDetail2PollAnalytics.setVisibility(View.VISIBLE);
                        btnRightPollAnalytics.setTag("true");
                        btnRightPollAnalytics.setImageDrawable(getDrawable(R.drawable.right));
                    } else if (txtDetail2PollAnalytics.getVisibility() == View.VISIBLE) {
                        txtDetail2PollAnalytics.setVisibility(View.GONE);
                        txtDetail1PollAnalytics.setVisibility(View.VISIBLE);
                        btnLeftPollAnalytics.setTag("false");
                        btnLeftPollAnalytics.setImageDrawable(getDrawable(R.drawable.left_disabled));
                    }
                }
            }
        });

        btnRightPollAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean enabled = Boolean.valueOf(btnRightPollAnalytics.getTag().toString());
                if (enabled){
                    if (txtDetail1PollAnalytics.getVisibility() == View.VISIBLE) {
                        txtDetail1PollAnalytics.setVisibility(View.GONE);
                        txtDetail2PollAnalytics.setVisibility(View.VISIBLE);
                        btnLeftPollAnalytics.setTag("true");
                        btnLeftPollAnalytics.setImageDrawable(getDrawable(R.drawable.left));
                    } else if (txtDetail2PollAnalytics.getVisibility() == View.VISIBLE) {
                        txtDetail2PollAnalytics.setVisibility(View.GONE);
                        txtDetail3PollAnalytics.setVisibility(View.VISIBLE);
                        btnRightPollAnalytics.setTag("false");
                        btnRightPollAnalytics.setImageDrawable(getDrawable(R.drawable.right_disabled));
                    }
                }
            }
        });

        btnDetailedAnalysisPollAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = null;
                String title = "";
                final RadioButton selected = radioGroup.getSelectedItem();
                if (selected == null){
                    if (txtDetail1PollAnalytics.getVisibility() == View.VISIBLE){
                        v = createAccordingToAllGender(oyla);
                        title = "Detaylı Analiz - Cinsiyete Göre";
                    } else if (txtDetail2PollAnalytics.getVisibility() == View.VISIBLE){
                        v = createAccordingToAllAge(oyla);
                        title = "Detaylı Analiz - Yaşa Göre";
                    } else {
                        v = createAccordingToAllCity(oyla);
                        title = "Detaylı Analiz - Yaşanılan Şehre Göre";
                    }
                } else {
                    final int optionId = (Integer) selected.getTag();
                    if (txtDetail1PollAnalytics.getVisibility() == View.VISIBLE){
                        v = createAccordingToGender(oyla, optionId);
                        title = "Detaylı Analiz - Cinsiyete Göre";
                    } else if (txtDetail2PollAnalytics.getVisibility() == View.VISIBLE){
                        v = createAccordingToAge(oyla, optionId);
                        title = "Detaylı Analiz - Yaşa Göre";
                    } else {
                        v = createAccordingToCity(oyla, optionId);
                        title = "Detaylı Analiz - Yaşanılan Şehre Göre";
                    }
                }
                final AlertDialog dialog = new AlertDialog.Builder(PollAnalyticsActivity.this)
                        .setTitle(title)
                        .setView(v)
                        .setPositiveButton("Kapat", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create();

                dialog.show();
            }
        });

        if (anonymous){
            txtUserCannotSeeDetailedAnalysis.setVisibility(View.VISIBLE);
            btnDetailedAnalysisPollAnalytics.setEnabled(false);
        }

        guiInitialized = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_analytics);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        setTitle(" Anket İstatistikleri");
        final OylaDatabase oyla = ((MyApplication) getApplication()).oyla();
        if (!guiInitialized) initializeGui(oyla);
    }

}
