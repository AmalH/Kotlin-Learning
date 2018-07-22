package amalhichri.androidprojects.com.kotlinlearning.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import amalhichri.androidprojects.com.kotlinlearning.R;

public class AboutUsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_us, container, false);
        v.findViewById(R.id.developerWebsiteBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://amalhichri.pragmatictheories.tech")));
            }
        });
        v.findViewById(R.id.developerLinkedInBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/amalhichri/")));
            }
        });
        v.findViewById(R.id.developerGitBtn).

            setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View v){ startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/AmalH")));
                }
            });
        return v;
        }
    }
