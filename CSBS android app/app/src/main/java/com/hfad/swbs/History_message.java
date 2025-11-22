package com.hfad.swbs;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Handler;
import android.preference.EditTextPreference;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link History_message#newInstance} factory method to
 * create an instance of this fragment.
 */
public class History_message extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PHOTO_PREFIX = "[Photo]";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean inSpotlight = false;

    private CardView largCardView;

    private TextView noDataText;
    private LinearLayout containerLayout;

    private TextView teacherLar;
    private TextView fromWhoLar;
    private TextView sendtimeLar;
    private TextView contentLar;
    private ImageView contentLarImage;
    private TextView readTextLar;
    private TextView classNum;
    private TextView message_len;
    private ProgressBar progressBar;
    private TextView nowtime;
    int height = 1000;
    int width = 1800;
    private ConstraintLayout constraintLayout;
    private ScrollView scrollView;
    TextPaint textPaint;

    private Handler handler;
    MyDatabaseHelper database;
    Button readbuttonLar;
    int isMessageOpen;

    public History_message() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment History_message.
     */
    // TODO: Rename and change types and number of parameters


    private final BroadcastReceiver isMessageActivityOpen = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MessageActivity_Open")) {
                isMessageOpen = intent.getIntExtra("is_open", 0);
                Log.d("isMessageOpen", "open:"+ isMessageOpen);
            }
        }
    };
    public static History_message newInstance(String param1, String param2) {
        History_message fragment = new History_message();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        IntentFilter intentFilter = new IntentFilter("MessageActivity_Open");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(isMessageActivityOpen, intentFilter);




    }

    private void startUpdatingTime() {
        // 创建一个 Runnable 来更新时间
        Runnable updateTimeRunnable = new Runnable() {
            @Override
            public void run() {
                // 获取当前时间
                Date currentTime = new Date();

                // 格式化时间
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
                String formattedTime = sdf.format(currentTime);

                // 设置给 TextView
                nowtime.setText(formattedTime);

                // 一段时间后再次执行此 Runnable
                handler.postDelayed(this, 1000); // 1000毫秒 = 1秒
            }
        };

        // 首次执行此 Runnable
        updateTimeRunnable.run();
    }


    @Override
    public void onResume() {
        super.onResume();

        handler = new Handler();
        startUpdatingTime();

        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // 获取 ScrollView 的宽度和高度
                width = scrollView.getWidth();
                height = scrollView.getHeight();
//                Log.d("width", width+"");
//                Log.d("height", height+"");
                // 在这里可以使用获取到的宽度和高度做一些操作

                // 移除监听器，以免重复获取
                scrollView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }




    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_message, container, false);
        database = new MyDatabaseHelper(requireContext());
        containerLayout = view.findViewById(R.id.container_layout);
        noDataText = view.findViewById(R.id.noDataText);
        largCardView = view.findViewById(R.id.larg_CardView);
        largCardView.setVisibility(View.INVISIBLE);
        noDataText.setVisibility(view.INVISIBLE);
        teacherLar = view.findViewById(R.id.teacherLar);
        fromWhoLar = view.findViewById(R.id.fromWhoLar);
        sendtimeLar = view.findViewById(R.id.sendtimeLar);
        contentLar = view.findViewById(R.id.contentLar);
        contentLarImage = view.findViewById(R.id.contentLarImage);
        readTextLar = view.findViewById(R.id.readTextLar);
        readbuttonLar = view.findViewById(R.id.readbutton);
        classNum = view.findViewById(R.id.class_num);
        message_len = view.findViewById(R.id.message_len);
        constraintLayout  = view.findViewById(R.id.constrainLayout);
        scrollView = view.findViewById(R.id.scrollView);
        progressBar = view.findViewById(R.id.progressBar);
        nowtime = view.findViewById(R.id.nowtime);

        textPaint = new TextPaint();

        progressBar.setVisibility(View.VISIBLE);
        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inSpotlight && isMessageOpen == 2) {
                    inSpotlight = false;
                    endSpotlightMode();
                    Log.d("containLayout", "Clicked");
                }
            }
        });




        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inSpotlight && isMessageOpen == 2) {
                    inSpotlight = false;
                    endSpotlightMode();
                    Log.d("containrLayout", "Clicked");
                }
            }
        });


        String className = database.getClassName();
        if (!Objects.equals(className, "-1")) {
            classNum.setText(className);
        }

        String[][] message;
        message = database.getMessage();
        Log.d("message list",  Arrays.deepToString(message));
        if (message == null || message.length == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            database.editMessageStat(4);
            isOpenBroadCast(4);
            noDataText.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            showCardView(message);
            message_len.setText("共" +message.length+"筆");

        }




        return view;
    }



    @SuppressLint("SetTextI18n")
    private void showCardView(String[][] message) {
        int Len = message.length;
        int heightInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 175, getResources().getDisplayMetrics());
        Log.d("heightInDp", heightInDp+"");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                heightInDp
        );


        layoutParams.setMargins(120, 2, 50, 30);
        for (int i = 0; i < Len; i++) {
            CardView cardView = new CardView(getContext());
            cardView.setLayoutParams(layoutParams);

            cardView.setCardElevation(8); // Set elevation/shadow if needed
            cardView.setRadius(60); // Set corner radius if needed
            // Add content inside the CardView
            // You can add any views or layouts inside the CardView as needed
            // For example, adding a TextView


            ConstraintLayout innerLayout = new ConstraintLayout(getContext());
            innerLayout.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            ));
            TextView teacher = new TextView(getContext());
            teacher.setId(View.generateViewId()); // Set an ID for constraints
            teacher.setTextColor(Color.parseColor("#ffffff"));
            teacher.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            teacher.setFontFeatureSettings("sans-serif-black");
            teacher.setText(message[i][0]);
            innerLayout.addView(teacher);

            TextView fromWhere = new TextView(getContext());
            fromWhere.setId(View.generateViewId()); // Set an ID for constraints
            fromWhere.setTextColor(Color.parseColor("#ffffff"));
            fromWhere.setFontFeatureSettings(String.valueOf(R.font.archivo_black));
            fromWhere.setText(message[i][1]);
            fromWhere.setTextSize(TypedValue.COMPLEX_UNIT_SP,35);
            innerLayout.addView(fromWhere);


            TextView readText = new TextView(getContext());
            readText.setId(View.generateViewId());
            readText.setTypeface(Typeface.DEFAULT_BOLD);

            if (database.checkIsNew(message[i][4]) == 3) {
                readText.setText("未讀");
                readText.setTextColor(ContextCompat.getColor(getContext(), R.color.HisUnread));
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.HistoryNewMessage));
            } else {
                readText.setText("已讀");
                readText.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
                cardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.HistoryMessage));

            }
            readText.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            innerLayout.addView(readText);

            TextView content = new TextView(getContext());
            content.setId(View.generateViewId()); // Set an ID for constraints
            content.setTextColor(Color.parseColor("#ffffff"));
//            content.setAutoLinkMask(Linkify.WEB_URLS);
//            message[i][2] = "測試測試\n測試測試測試測試測試設次社測測試餓餓次社測試測試餓餓次社測試測試餓餓次社測試測試";
            Log.d("message", message[i][2]);
            Log.d("countLines", String.valueOf(countLines(message[i][2])));
            content.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
            content.setPadding(120,2,100,80);
            ConstraintLayout.LayoutParams contentParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            content.setLayoutParams(contentParams);
            innerLayout.addView(content);

            ImageView contentImage = new ImageView(getContext());
            contentImage.setId(View.generateViewId());
            contentImage.setAdjustViewBounds(true);
            contentImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            contentImage.setVisibility(View.GONE);
            contentImage.setContentDescription(getString(R.string.photo_message_description));
            ConstraintLayout.LayoutParams imageParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );
            contentImage.setLayoutParams(imageParams);
            innerLayout.addView(contentImage);

            displayCardMessageContent(message[i][2], content, contentImage);

            TextView sendtime = new TextView(getContext());
            sendtime.setId(View.generateViewId()); // Set an ID for constraints
            sendtime.setTextColor(Color.parseColor("#ffffff"));
            sendtime.setText(message[i][3]);
            sendtime.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
            innerLayout.addView(sendtime);

            // Apply constraints for TextViews inside innerLayout
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(innerLayout);

            constraintSet.connect(fromWhere.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 100);
            constraintSet.connect(fromWhere.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);

            constraintSet.connect(teacher.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50);
            constraintSet.connect(teacher.getId(), ConstraintSet.LEFT, fromWhere.getId(), ConstraintSet.RIGHT, 60);
//            constraintSet.connect(teacher.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 40);

            int widthInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 800, getResources().getDisplayMetrics());
            constraintSet.connect(sendtime.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, widthInDp);
            constraintSet.connect(sendtime.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 100);
            constraintSet.connect(sendtime.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 40);

            constraintSet.connect(content.getId(), ConstraintSet.TOP, teacher.getId(), ConstraintSet.BOTTOM, 20);
            constraintSet.connect(content.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 60);
            constraintSet.connect(content.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 60);
            constraintSet.connect(content.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 80);

            constraintSet.connect(contentImage.getId(), ConstraintSet.TOP, teacher.getId(), ConstraintSet.BOTTOM, 20);
            constraintSet.connect(contentImage.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 60);
            constraintSet.connect(contentImage.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 60);
            constraintSet.connect(contentImage.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 80);

            constraintSet.connect(readText.getId(), ConstraintSet.LEFT, teacher.getId(), ConstraintSet.RIGHT, 60);
            constraintSet.connect(readText.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 50);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMessageOpen = database.checkMessageStat();
                    if (inSpotlight && isMessageOpen == 2) {
                        inSpotlight = false;
                        endSpotlightMode();

                    }
                    else if (isMessageOpen == 2) {
                        inSpotlight = true;
                        enlargeAndHighlightCardView(cardView, message);
                    }

                    // 点击 CardView 时的处理逻辑



                }
            });


            constraintSet.applyTo(innerLayout);
            cardView.addView(innerLayout); // Add innerLayout to CardView
            cardView.setId(i);
            containerLayout.addView(cardView);
        }

    }


    private void displayCardMessageContent(String messageText, TextView contentView, ImageView contentImageView) {
        if (messageText == null) {
            messageText = "";
        }

        if (isPhotoMessage(messageText)) {
            String url = extractPhotoUrl(messageText);
            if (!TextUtils.isEmpty(url)) {
                contentView.setVisibility(View.GONE);
                contentView.setText("");
                contentImageView.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .into(contentImageView);
                return;
            }
            String fallbackText = messageText.substring(PHOTO_PREFIX.length()).trim();
            if (!TextUtils.isEmpty(fallbackText)) {
                messageText = fallbackText;
            }
        }

        Glide.with(this).clear(contentImageView);
        contentImageView.setImageDrawable(null);
        contentImageView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);

        String preview = truncateStringToLines(messageText, 2);
        if (preview.length() > 29) {
            preview = preview.substring(0, Math.min(preview.length(), 23));
            preview += "......";
            contentView.setText(Html.fromHtml(preview));
        } else {
            contentView.setText(preview);
        }
    }


    private void displayLargeMessageContent(String messageText) {
        if (messageText == null) {
            messageText = "";
        }

        if (isPhotoMessage(messageText)) {
            String url = extractPhotoUrl(messageText);
            if (!TextUtils.isEmpty(url)) {
                contentLar.setVisibility(View.GONE);
                contentLar.setText("");
                contentLarImage.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(url)
                        .into(contentLarImage);
                return;
            }
            String fallbackText = messageText.substring(PHOTO_PREFIX.length()).trim();
            if (!TextUtils.isEmpty(fallbackText)) {
                messageText = fallbackText;
            }
        }

        Glide.with(this).clear(contentLarImage);
        contentLarImage.setImageDrawable(null);
        contentLarImage.setVisibility(View.GONE);
        contentLar.setVisibility(View.VISIBLE);
        contentLar.setText(messageText);
        contentLar.setLinkTextColor(ContextCompat.getColor(requireContext(), R.color.url));
    }

    private boolean isPhotoMessage(String messageText) {
        return !TextUtils.isEmpty(messageText) && messageText.startsWith(PHOTO_PREFIX);
    }

    private String extractPhotoUrl(String messageText) {
        if (TextUtils.isEmpty(messageText) || !messageText.startsWith(PHOTO_PREFIX)) {
            return "";
        }
        String urlPart = messageText.substring(PHOTO_PREFIX.length()).trim();
        int endIndex = urlPart.length();
        int spaceIndex = urlPart.indexOf(' ');
        if (spaceIndex >= 0) {
            endIndex = Math.min(endIndex, spaceIndex);
        }
        int newlineIndex = urlPart.indexOf('\n');
        if (newlineIndex >= 0) {
            endIndex = Math.min(endIndex, newlineIndex);
        }
        int bracketIndex = urlPart.indexOf('(');
        if (bracketIndex >= 0) {
            endIndex = Math.min(endIndex, bracketIndex);
        }
        int fullWidthBracketIndex = urlPart.indexOf('（');
        if (fullWidthBracketIndex >= 0) {
            endIndex = Math.min(endIndex, fullWidthBracketIndex);
        }
        String url = urlPart.substring(0, Math.max(0, endIndex)).trim();
        return trimTrailingPunctuation(url);
    }

    private String trimTrailingPunctuation(String input) {
        if (TextUtils.isEmpty(input)) {
            return "";
        }
        int end = input.length();
        while (end > 0) {
            char c = input.charAt(end - 1);
            if (c == ')' || c == ']' || c == '>' || c == '}' || c == '，' || c == ',' || c == '。' || c == '.' || c == '）' || c == '】' || c == '》') {
                end--;
            } else {
                break;
            }
        }
        return input.substring(0, end);
    }


    public static int countLines(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        // 將字串按換行符分割成字符串數組
        String[] lines = input.split("\n");

        // 返回字符串數組的長度，即行數
        return lines.length;
    }

    public static String truncateStringToLines(String input, int maxLines) {
        if (input == null || maxLines <= 0) {
            return "";
        }

        String[] lines = input.split("\n");

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(maxLines, lines.length); i++) {
            result.append(lines[i]);
            if (i < maxLines - 1) {
                result.append("\n");
            }
        }

        return result.toString();
    }



    private void enlargeAndHighlightCardView(CardView cardView, String[][] message) {
        int id = cardView.getId();
        largCardView.setVisibility(View.VISIBLE);
        teacherLar.setText(message[id][0]);
        fromWhoLar.setText(message[id][1]);
        sendtimeLar.setText(message[id][3]);
        displayLargeMessageContent(message[id][2]);

        if (database.checkIsNew(message[id][4]) == 3) {
            readTextLar.setText("未讀");
            readTextLar.setTextColor(ContextCompat.getColor(getContext(), R.color.HisUnread));
            readbuttonLar.setVisibility(View.VISIBLE);
            largCardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.HistoryNewMessage));
        }
        else {
            readTextLar.setText("已讀");
            readTextLar.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
            readbuttonLar.setVisibility(View.INVISIBLE);
            largCardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.HistoryMessage));

        }
        readbuttonLar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.editIsNew(message[id][4], 0);
                readbuttonLar.setVisibility(View.INVISIBLE);
                readTextLar.setText("已讀");
                readTextLar.setTextColor(ContextCompat.getColor(getContext(), R.color.read));
                largCardView.setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.HistoryMessage));

            }
        });

        // 更改背景透明度，使其他 CardView 变暗
        for (int i = 0; i < containerLayout.getChildCount(); i++) {
            View childView = containerLayout.getChildAt(i);
            if (childView instanceof CardView) {
                ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(childView, "alpha", 4.0f, 0.0f);
                alphaAnimator.setDuration(10);
                alphaAnimator.start();
            }
        }
    }

    public void endSpotlightMode(){

            largCardView.setVisibility(View.INVISIBLE);

            for (int i = 0; i < containerLayout.getChildCount(); i++) {
                View childView = containerLayout.getChildAt(i);
                if (childView instanceof CardView) {
                    ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(childView, "alpha", 1.5f, 1f);
                    alphaAnimator.setDuration(150);
                    alphaAnimator.start();
                }
            }
            if (containerLayout != null) {
                containerLayout.removeAllViews();
                showCardView(database.getMessage());
            }


    }

    private void isOpenBroadCast(int isOpen) {
        Intent MessageOpen = new Intent("MessageActivity_Open");
        MessageOpen.putExtra("is_open", isOpen);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(MessageOpen);
    }



}