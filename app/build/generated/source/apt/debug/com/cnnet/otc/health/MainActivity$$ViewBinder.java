// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MainActivity$$ViewBinder<T extends com.cnnet.otc.health.MainActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131623940, "field 'home'");
    target.home = finder.castView(view, 2131623940, "field 'home'");
    view = finder.findRequiredView(source, 2131624082, "field 'member'");
    target.member = finder.castView(view, 2131624082, "field 'member'");
    view = finder.findRequiredView(source, 2131624085, "field 'myDevice'");
    target.myDevice = finder.castView(view, 2131624085, "field 'myDevice'");
  }

  @Override public void unbind(T target) {
    target.home = null;
    target.member = null;
    target.myDevice = null;
  }
}
