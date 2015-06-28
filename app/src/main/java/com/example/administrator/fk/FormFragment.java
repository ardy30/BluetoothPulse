package com.example.administrator.fk;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class FormFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText etFormName = null;
    private EditText etFormAge = null;
    private RadioButton rbFormNan = null;
    private RadioButton rbFormNu = null;
    private Button btnCancel = null;
    private Button btnOk = null;


    private String mParam1;
    private String mParam2;


    public static FormFragment newInstance(String param1, String param2) {
        FormFragment fragment = new FormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form, container, false);

        etFormName = (EditText)rootView.findViewById(R.id.etForm1);
        etFormAge = (EditText)rootView.findViewById(R.id.etForm2);
        rbFormNan = (RadioButton)rootView.findViewById(R.id.rbForm1);
        rbFormNu = (RadioButton)rootView.findViewById(R.id.rbForm2);
        btnCancel = (Button)rootView.findViewById(R.id.btnForm1);
        btnOk = (Button)rootView.findViewById(R.id.btnForm2);
        btnCancel.setOnClickListener(new Myclick());
        btnOk.setOnClickListener(new Myclick());
        return rootView;
    }

    //要在类的里边CTRL+O
    class Myclick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(checkForm()){
                if(view.getId()==R.id.btnForm1){
                    writeForm();
                    toMainPage();
                }
                if(view.getId()==R.id.btnForm2){
                    writeForm();
                    toDetailPage();
                }
            }
        }
    }

    //检查表单
    public boolean checkForm(){
        if(etFormName.getText().toString().trim()==""&&
                etFormAge.getText().toString().trim()==""){
            toastDisplay("请完整填写表单！");
            return false;
        }else{
            return true;
        }
    }

    public void writeForm(){
        PersonInfo.name = etFormName.getText().toString();
        PersonInfo.age = Integer.parseInt(etFormAge.getText().toString());
        if(!rbFormNan.isChecked()){
            PersonInfo.gender = "女";
        }
    }

    //显示细节Fragment
    public void toDetailPage(){
        DetailFragment df = new DetailFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container,df);
        ft.addToBackStack(null);
        ft.commit();
        //开始绘制
        CommonList.isStopDraw = false;
        //清空之前的数据
        CommonList.comData.clear();
        CommonList.returnData.clear();
        PersonInfo.isRecord = true;
    }

    //切换到MainFragment
    public void toMainPage(){
        MainFragment mf = new MainFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container, mf);
        ft.addToBackStack(null);
        ft.commit();
        PersonInfo.isRecord = true;
    }

    //显示信息
    public void toastDisplay(String msg){
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
