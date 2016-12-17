// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class ServerSettingActivity$$ViewBinder<T extends com.cnnet.otc.health.activities.ServerSettingActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624097, "field 'ivBackBtn'");
    target.ivBackBtn = finder.castView(view, 2131624097, "field 'ivBackBtn'");
    view = finder.findRequiredView(source, 2131624098, "field 'etServerUrl'");
    target.etServerUrl = finder.castView(view, 2131624098, "field 'etServerUrl'");
    view = finder.findRequiredView(source, 2131624099, "field 'btnServerSetting'");
    target.btnServerSetting = finder.castView(view, 2131624099, "field 'btnServerSetting'");
  }

  @Override public void unbind(T target) {
    target.ivBackBtn = null;
    target.etServerUrl = null;
    target.btnServerSetting = null;
  }
}
