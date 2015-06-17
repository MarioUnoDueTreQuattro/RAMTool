package com.thepriest.andrea.ramtool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/*
 * Class to show a changelog dialog
 * (c) 2012 Martin van Zuilekom (http://martin.cubeactive.com)
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

public class ChangeLogDialog {
    static final private String TAG = "ChangeLogDialog";

    static final private String TITLE_CHANGELOG = "title_changelog";
    static final private String CHANGELOG_XML = "changelog";

    private Activity fActivity;

    public ChangeLogDialog(Activity context) {
        fActivity = context;
    }

    //Get the current app version
    private String GetAppVersion() {
        try {
            PackageInfo _info = fActivity.getPackageManager().getPackageInfo(fActivity.getPackageName(), 0);
            return _info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    //Parse a the release tag and return html code
    private String ParseReleaseTag(XmlResourceParser aXml) throws XmlPullParserException, IOException {
        String _Result = "<h1>Version " + aXml.getAttributeValue(null, "version") + "</h1><ul>";
        int eventType = aXml.getEventType();
        while ((eventType != XmlPullParser.END_TAG) || (aXml.getName().equals("change"))) {
            if ((eventType == XmlPullParser.START_TAG) && (aXml.getName().equals("change"))) {
                eventType = aXml.next();
                _Result = _Result + "<li>" + aXml.getText() + "</li>";
            }
            eventType = aXml.next();
        }
        _Result = _Result + "</ul>";
        return _Result;
    }

    //CSS style for the html
    private String GetStyle() {
        return
                "<style type=\"text/css\">"
                        +"body {background-color: #222222; }"
                        +"h1 {color: #eeeeee; }"
                        +"li {color: #bebebe; }"
                        +"ul {color: #bebebe; }"
                        + "h1 { margin-left: 0px; font-size: 14pt; }"
                        + "li { margin-left: 0px; font-size: 10pt; }"
                        + "ul { padding-left: 30px;}"
                        + "</style>";
    }

    //Get the changelog in html code, this will be shown in the dialog's webview
    private String GetHTMLChangelog(int aResourceId, Resources aResource) {
        String _Result = "<html><head>" + GetStyle() + "</head><body>";
        XmlResourceParser _xml = aResource.getXml(aResourceId);
        try {
            int eventType = _xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if ((eventType == XmlPullParser.START_TAG) && (_xml.getName().equals("release"))) {
                    _Result = _Result + ParseReleaseTag(_xml);

                }
                eventType = _xml.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);

        } finally {
            _xml.close();
        }
        _Result = _Result + "</body></html>";
        return _Result;
    }

    //Call to show the changelog dialog
    public void show() {
        //Get resources
        String _PackageName = fActivity.getPackageName();
        Resources _Resource;
        try {
            _Resource = fActivity.getPackageManager().getResourcesForApplication(_PackageName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return;
        }

        //Get dialog title	        	
        int _resID = _Resource.getIdentifier(TITLE_CHANGELOG, "string", _PackageName);
        String _Title = _Resource.getString(_resID);
        _Title = _Title + " " + GetAppVersion();

        //Get Changelog xml resource id
        _resID = _Resource.getIdentifier(CHANGELOG_XML, "xml", _PackageName);
        //Create html change log
        String _HTML = GetHTMLChangelog(_resID, _Resource);

        //Get button strings
        String _Close = _Resource.getString(R.string.changelog_close);

        //Check for empty changelog
        if (_HTML.equals("") == true) {
            //Could not load change log, message user and exit void
            Toast.makeText(fActivity, fActivity.getString(R.string.could_not_load_change_log), Toast.LENGTH_SHORT).show();
            return;
        }

        //Create webview and load html
        WebView _WebView = new WebView(fActivity);
        //fActivity.setTheme(R.style.AppTheme);
        _WebView.loadData(_HTML, "text/html", "utf-8");
        AlertDialog.Builder builder = new AlertDialog.Builder(fActivity)
                .setTitle(_Title)
                .setView(_WebView)
                .setPositiveButton(_Close, new Dialog.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

}
