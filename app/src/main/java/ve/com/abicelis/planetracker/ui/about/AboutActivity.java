package ve.com.abicelis.planetracker.ui.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import ve.com.abicelis.planetracker.R;
import ve.com.abicelis.planetracker.data.local.SharedPreferenceHelper;


/**
 * Created by abice on 29/3/2017.
 */

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.activity_about_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.activity_about_version)
    TextView mVersion;
    @BindView(R.id.activity_about_author)
    TextView mAuthor;
    @BindView(R.id.activity_about_website_link)
    TextView mWebsiteLink;
    @BindView(R.id.activity_about_market_link)
    TextView mMarketLink;
    @BindView(R.id.activity_about_github_link)
    TextView mGithubLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(new SharedPreferenceHelper().getAppThemeType().getTheme());
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.activity_about_toolbar_title);
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_arrow_back));
        setSupportActionBar(mToolbar);


        mVersion.setText(String.format(Locale.getDefault(), getResources().getString(R.string.activity_about_version), getAppVersionAndBuild(this).first));
        mAuthor.setText(String.format(Locale.getDefault(), getResources().getString(R.string.activity_about_author), Calendar.getInstance().get(Calendar.YEAR)));



        mMarketLink.setOnClickListener(v -> {
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW);
                playStoreIntent.setData(Uri.parse(getResources().getString(R.string.url_market)));
                startActivity(playStoreIntent);
        });
        mGithubLink.setOnClickListener(v -> {
                launchWebBrowser(AboutActivity.this, getResources().getString(R.string.url_github));
        });
        mWebsiteLink.setOnClickListener(v -> {
                launchWebBrowser(AboutActivity.this, getResources().getString(R.string.url_website));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    public static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Toast.makeText(context, "Could not find a Browser to open link", Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Could not start web browser", Toast.LENGTH_SHORT).show();

            return false;
        }
    }
}
