package com.example.todolist;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class TasksToListView extends ArrayAdapter<String> {
    //Bu sınıf arrayler içindeki veriyi listview'a aktarmak için array adapter'dan kalıtılarak oluşturuldu.

    private final ArrayList<String> Titles;
    private final ArrayList<String> Content;
    private final Activity context;

    public TasksToListView(ArrayList<String> title,ArrayList<String> content,Activity context) {
        super(context,R.layout.custom_list_view,title);
        //Constructor oluşturup nesne oluşturulurken yazdırılacak olan arrayleri parametre olarak veriyoruz.
        this.Titles = title;
        this.Content = content;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.custom_list_view,null,true);

        //Custom view içindeki view elemanlarını değişkene aktarıyoruz.
        TextView titleText = customView.findViewById(R.id.txtTitle);
        TextView contentText = customView.findViewById(R.id.txtContent);

        //Bu kısımda da hangi array içerisindeki verinin hangi view elemanına yazdırılacağını tanımlıyoruz.
        titleText.setText(Titles.get(position));
        contentText.setText(Content.get(position));

        return customView;
    }
}
