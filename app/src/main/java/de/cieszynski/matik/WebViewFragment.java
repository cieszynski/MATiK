package de.cieszynski.matik;

import android.animation.ObjectAnimator;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.webkit.WebViewCompat;

import com.google.android.material.navigation.NavigationView;

public class WebViewFragment extends Fragment implements View.OnLongClickListener {

    private WebView webView;
    private Toolbar toolbar;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_webview, container, false);

        toolbar = view.findViewById(R.id.toolbar);

        webView = view.findViewById(R.id.webview);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                int[] level = new int[]{
                        Log.DEBUG,  // DEBUG
                        Log.ERROR,  // ERROR
                        Log.INFO,   // LOG
                        Log.INFO,   // TIP?
                        Log.WARN    // WARN
                };
                String tag = String.format("WebViewFragment: %s %d",
                        URLUtil.guessFileName(consoleMessage.sourceId(), null, null),
                        consoleMessage.lineNumber());
                Log.println(level[consoleMessage.messageLevel().ordinal()], tag, consoleMessage.message());
                return true;
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Bundle bundle = new Bundle();
                bundle.putString("url", url);
                NavOptions navOptions = new NavOptions.Builder()
                        .setEnterAnim(R.anim.nav_default_enter_anim)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
                        .build();
                Navigation.findNavController(view).navigate(R.id.detailFragment, bundle, navOptions);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                toolbar.setTitle(view.getTitle());
            }
        });

        webView.addJavascriptInterface(new Object() {

            @JavascriptInterface
            public void showToast(String toast) {
                Toast.makeText(getContext(), toast, Toast.LENGTH_SHORT).show();
            }

            @JavascriptInterface
            public String version() {
                PackageInfo packageInfo = WebViewCompat.getCurrentWebViewPackage(getActivity());
                return packageInfo.versionName;
            }
        }, "android");

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(getArguments().getString("url", "file:///android_asset/crosslist.html"));
        }

        webView.setOnLongClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        toolbar = view.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = getActivity().findViewById(R.id.drawer);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_viewpager)
                .setDrawerLayout(drawerLayout)
                .build();

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            return NavigationUI.onNavDestinationSelected(menuItem, navController);
        });

        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        webView.saveState(outState);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final int colorFrom = getResources().getColor(R.color.windowBackground);
        final int colorTo = getResources().getColor(R.color.transparent);

        Toolbar toolbar = getView().findViewById(R.id.toolbar);
        NestedScrollView nestedScrollView = getView().findViewById(R.id.nestedscrollview);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY < 5) {
                ObjectAnimator colorFade = ObjectAnimator.ofObject(toolbar, "backgroundColor",
                        new ArgbEvaluator(), colorFrom, colorTo);
                colorFade.setDuration(500);
                colorFade.start();
            } else {
                toolbar.setBackgroundResource(R.color.windowBackground);
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        Log.d("XXX", "onLongClick");
        WebView.HitTestResult result = webView.getHitTestResult();
        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            Bundle bundle = new Bundle();
            bundle.putString("url", result.getExtra());

            BottomSheetFragment fragment = BottomSheetFragment.newInstance(bundle);
            fragment.show(getActivity().getSupportFragmentManager(), "TAG");
            Log.d("XXX", result.getExtra());
        } else {

            Log.d("XXX", String.valueOf(result.getType()));
        }
        return true;
    }
}
