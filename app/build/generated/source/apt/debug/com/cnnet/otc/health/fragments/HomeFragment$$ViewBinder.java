// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class HomeFragment$$ViewBinder<T extends com.cnnet.otc.health.fragments.HomeFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624043, "field 'listView'");
    target.listView = finder.castView(view, 2131624043, "field 'listView'");
    view = finder.findRequiredView(source, 2131624146, "method 'unRegister'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.unRegister();
        }
      });
  }

  @Override public void unbind(T target) {
    target.listView = null;
  }
}
