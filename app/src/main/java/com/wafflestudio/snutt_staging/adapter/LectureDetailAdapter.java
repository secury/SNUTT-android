package com.wafflestudio.snutt_staging.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.ClassTime;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.LectureItem;
import com.wafflestudio.snutt_staging.model.Table;
import com.wafflestudio.snutt_staging.ui.LectureMainActivity;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.wafflestudio.snutt_staging.model.LectureItem.ViewType.ItemButton;
import static com.wafflestudio.snutt_staging.model.LectureItem.ViewType.ItemHeader;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureDetailAdapter extends BaseAdapter {
    private ArrayList<LectureItem> lists;
    private Activity activity;
    private Lecture lecture;

    private int day;
    private int fromTime;
    private int toTime;

    private final static String TAG = "LECTURE_DETAIL_ADAPTER";

    public LectureDetailAdapter(Activity activity, ArrayList<LectureItem> lists, Lecture lecture) {
        this.activity = activity;
        this.lists = lists;
        this.lecture = lecture;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public LectureItem getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, final ViewGroup viewGroup) {
        final LectureItem item = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        LectureItem.ViewType type = lists.get(position).getViewType();
        switch (type) {
            case ItemHeader:
                view = inflater.inflate(R.layout.cell_lecture_header, viewGroup, false);
                break;
            case ItemTitle:
                view = inflater.inflate(R.layout.cell_lecture_item_title, viewGroup, false);
                break;
            case ItemDetail:
                view = inflater.inflate(R.layout.cell_lecture_item_detail, viewGroup, false);
                break;
            case ItemButton:
                view = inflater.inflate(R.layout.cell_lecture_item_button, viewGroup, false);
                break;
            case ItemColor:
                view = inflater.inflate(R.layout.cell_lecture_item_color, viewGroup, false);
                break;
            case ItemClass:
                view = inflater.inflate(R.layout.cell_lecture_item_class, viewGroup, false);
                break;
        }
        switch (type) {
            case ItemTitle: {
                TextView title = (TextView) view.findViewById(R.id.text_title);
                EditText value = (EditText) view.findViewById(R.id.text_value);
                title.setText(item.getTitle1());
                value.setText(item.getValue1());
                value.setClickable(item.isEditable());
                value.setFocusable(item.isEditable());
                value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue1(s.toString());
                    }
                });
                break;
            }
            case ItemDetail: {
                TextInputLayout title1 = (TextInputLayout) view.findViewById(R.id.input_title1);
                EditText editText1 = (EditText) view.findViewById(R.id.input_detail1);
                title1.setHint(item.getTitle1());
                editText1.setText(item.getValue1());
                editText1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue1(s.toString());
                    }
                });
                TextInputLayout title2 = (TextInputLayout) view.findViewById(R.id.input_title2);
                EditText editText2 = (EditText) view.findViewById(R.id.input_detail2);
                title2.setHint(item.getTitle2());
                editText2.setText(item.getValue2());
                editText2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setValue2(s.toString());
                    }
                });
                editText1.setClickable(item.isEditable());
                editText1.setFocusable(item.isEditable());
                editText2.setClickable(item.isEditable());
                editText2.setFocusable(item.isEditable());
                if (item.getType() == LectureItem.Type.AcademicYearCredit) { // 학점
                    editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                break;
            }
            case ItemButton: {
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
                TextView textView = (TextView) view.findViewById(R.id.text_button);
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (item.getType()) {
                            case Syllabus:
                                startSyllabus();
                                break;
                            case Remove:
                                startAlertView();
                                break;
                            default:
                                break;
                        }
                    }
                });
                switch (item.getType()) {
                    case Syllabus:
                        textView.setText("강의계획서");
                        textView.setTextColor(Color.parseColor("#000000"));
                        break;
                    case Remove:
                        textView.setText("삭제");
                        textView.setTextColor(Color.parseColor("#FF0000"));
                        break;
                    default:
                        break;
                }
                break;
            }
            case ItemColor: {
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
                TextView title = (TextView) view.findViewById(R.id.text_title);
                View fgColor = (View) view.findViewById(R.id.fgColor);
                View bgColor = (View) view.findViewById(R.id.bgColor);
                title.setText("색상");
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isEditable()) {
                            ((LectureMainActivity) activity).setColorPickerFragment();
                        }
                    }
                });
                bgColor.setBackgroundColor(item.getColor().getBg());
                fgColor.setBackgroundColor(item.getColor().getFg());
                break;
            }
            case ItemClass: {
                EditText editText1 = (EditText) view.findViewById(R.id.input_time);
                editText1.setHint("시간");
                String time = SNUTTUtils.numberToWday(item.getClassTime().getDay()) + " " +
                        SNUTTUtils.numberToTime(item.getClassTime().getStart()) + "~" +
                        SNUTTUtils.numberToTime(item.getClassTime().getStart() + item.getClassTime().getLen());
                editText1.setText(time);
                editText1.setClickable(false);
                editText1.setFocusable(false);
                editText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.isEditable()) {
                            showDialog(viewGroup, item);
                        }
                    }
                });
                EditText editText2 = (EditText) view.findViewById(R.id.input_location);
                editText2.setHint("장소");
                editText2.setText(item.getClassTime().getPlace());
                editText2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override
                    public void afterTextChanged(Editable s) {
                        item.getClassTime().setPlace(s.toString());
                    }
                });
                editText2.setClickable(item.isEditable());
                editText2.setFocusable(item.isEditable());
                break;
            }
        }
        return view;
    }

    private void showDialog(ViewGroup viewGroup, final LectureItem item) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // item's class time update
                ClassTime t = new ClassTime(day, fromTime / 2f, (toTime-fromTime) / 2f, item.getClassTime().getPlace());
                item.setClassTime(t);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View layout = inflater.inflate(R.layout.dialog_time_picker, null);
        alert.setView(layout);
        alert.show();

        NumberPicker dayPicker = (NumberPicker) layout.findViewById(R.id.dayPicker);
        NumberPicker fromPicker = (NumberPicker) layout.findViewById(R.id.timeFrom);
        final NumberPicker toPicker = (NumberPicker) layout.findViewById(R.id.timeTo);

        day = item.getClassTime().getDay();
        String[] days = {"월", "화", "수", "목", "금", "토", "일"};
        dayPicker.setMinValue(0);
        dayPicker.setMaxValue(6);
        dayPicker.setDisplayedValues(days);
        dayPicker.setValue(day);
        dayPicker.setWrapSelectorWheel(false);
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                day = newVal;
            }
        });

        // used integer interval (origin value * 2) to use number picker
        fromTime = (int) (item.getClassTime().getStart() * 2);
        String[] from = SNUTTUtils.getTimeList(0, 27);
        fromPicker.setMinValue(0);
        fromPicker.setMaxValue(27);
        fromPicker.setDisplayedValues(from);
        fromPicker.setValue(fromTime);
        fromPicker.setWrapSelectorWheel(false);
        fromPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                fromTime = newVal;
                /* set DisplayedValues as null to avoid out of bound index error */
                toPicker.setDisplayedValues(null);
                toPicker.setValue(fromTime + 1);
                toPicker.setMinValue(fromTime + 1);
                toPicker.setMaxValue(28);
                toPicker.setDisplayedValues(SNUTTUtils.getTimeList(fromTime + 1, 28));
                /* setValue method does not call listener, so we have to change the value manually */
                toTime = fromTime + 1;
            }
        });

        toTime = (int) (item.getClassTime().getStart()+item.getClassTime().getLen())*2;
        String[] to = SNUTTUtils.getTimeList(fromTime+1, 28);
        toPicker.setMinValue(fromTime+1);
        toPicker.setMaxValue(28);
        toPicker.setDisplayedValues(to);
        toPicker.setValue(toTime);
        toPicker.setWrapSelectorWheel(false);
        toPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                toTime = newVal;
            }
        });
    }

    public void updateLecture(Lecture lecture, Callback<Table> callback) {
        // 강의명, 교수, 학과, 학년, 학점, 분류, 구분, 강의시간 전체를 다 업데이트
        Log.d(TAG, "update lecture called.");
        Lecture target = new Lecture();
        JsonArray ja = new JsonArray();
        for (LectureItem item : lists) {
            if (item.getViewType() == ItemHeader) continue;
            if (item.getViewType() == ItemButton) continue;
            LectureItem.Type type = item.getType();

            switch (type) {
                case Title: // 강의명
                    target.setCourse_title(item.getValue1());
                    break;
                case Instructor: // 교수
                    target.setInstructor(item.getValue1());
                    break;
                case Color: // 색상
                    target.setBgColor(item.getColor().getBg());
                    target.setFgColor(item.getColor().getFg());
                    break;
                case Department: // 학과
                    target.setDepartment(item.getValue1());
                    break;
                case AcademicYearCredit: // 학년, 학점
                    target.setAcademic_year(item.getValue1());
                    target.setCredit(Integer.parseInt(item.getValue2()));
                    break;
                case ClassificationCategory: // 분류, 구분
                    target.setClassification(item.getValue1());
                    target.setCategory(item.getValue2());
                    break;
                case CourseNumberLectureNumber: // 강좌번호, 분반번호
                    break;
                case ClassTime:
                    JsonElement je = new Gson().toJsonTree(item.getClassTime());
                    ja.add(je);
                    break;
                default:
                    break;
            }
        }
        target.setClass_time_json(ja);
        LectureManager.getInstance().updateLecture(lecture, target, callback);
    }

    private void startSyllabus() {
        LectureManager.getInstance().getCoursebookUrl(lecture.getCourse_number(), lecture.getLecture_number(), new Callback<Map>() {
            public void success(Map map, Response response) {
                String url = (String) map.get("url");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(intent);
            }
            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    private void startAlertView() {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("강좌 삭제");
        alert.setMessage("강좌를 삭제하시겠습니까");
        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                LectureManager.getInstance().removeLecture(lecture, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        activity.finish();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }
}
