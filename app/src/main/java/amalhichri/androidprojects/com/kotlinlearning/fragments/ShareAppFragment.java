package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;


public class ShareAppFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_share_app, container, false);

        // share app through email
        (v.findViewById(R.id.shareAppEmailBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"amal.hichri@esprit.tn"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Learning Kotlin is now much funnier");
                email.putExtra(Intent.EXTRA_TEXT, "Hello! Check out on this new Kotlin learning app I found ! Join me there to collect Koltin trophies! _" +
                        "\n https://play.google.com/store/apps/details?id=com.zupilupi.sourcecodeviewer");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Send from: "));
            }
        });

        // share app through sms
        (v.findViewById(R.id.shareAppSmsBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("smsto:0021654821200");
                Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                it.putExtra("sms_body", "Here you can set the SMS text to be sent");
                startActivity(it);
            }
        });

        // share app
        (v.findViewById(R.id.shareAppShareBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,"Hello! Check out on this new Kotlin learning app I found ! Join me there to collect Koltin trophies! _" +
                        "\n https://play.google.com/store/apps/details?id=com.zupilupi.sourcecodeviewer");
                startActivity(Intent.createChooser(shareIntent, " share "));
            }
        });

        return v;
    }

}
