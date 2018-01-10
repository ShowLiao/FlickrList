package com.example.show.testflicker;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.DialogFragment;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class SearchDialogFragment extends DialogFragment {

    private EditText editText;
    Button searchBtn;
    public SearchDialogFragment(){}

    public static SearchDialogFragment newInstance(String title) {
        SearchDialogFragment dia = new SearchDialogFragment();
        Bundle args = new Bundle();
        args.putString("Search", title);
        dia.setArguments(args);
        return dia;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_search_dialog_fragment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editText = view.findViewById(R.id.dia_edit_search_key);

        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        searchBtn = view.findViewById(R.id.dia_search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("key");
                String str = editText.getText().toString();
                Log.e("====search", str);
                intent.putExtra("search",str);
                getActivity().sendBroadcast(intent);
                getDialog().dismiss();
            }
        });
    }

//
}
