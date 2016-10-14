// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MemberFragment$$ViewBinder<T extends com.cnnet.otc.health.fragments.MemberFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558587, "field 'memberTopBar'");
    target.memberTopBar = finder.castView(view, 2131558587, "field 'memberTopBar'");
    view = finder.findRequiredView(source, 2131558590, "field 'memberSearchBar'");
    target.memberSearchBar = finder.castView(view, 2131558590, "field 'memberSearchBar'");
    view = finder.findRequiredView(source, 2131558589, "field 'btnSearchMember'");
    target.btnSearchMember = finder.castView(view, 2131558589, "field 'btnSearchMember'");
    view = finder.findRequiredView(source, 2131558594, "field 'btnQuitSearch'");
    target.btnQuitSearch = finder.castView(view, 2131558594, "field 'btnQuitSearch'");
    view = finder.findRequiredView(source, 2131558588, "field 'btnAddMember'");
    target.btnAddMember = finder.castView(view, 2131558588, "field 'btnAddMember'");
    view = finder.findRequiredView(source, 2131558507, "field 'listview'");
    target.listview = finder.castView(view, 2131558507, "field 'listview'");
    view = finder.findRequiredView(source, 2131558591, "field 'etSearchNameLike'");
    target.etSearchNameLike = finder.castView(view, 2131558591, "field 'etSearchNameLike'");
    view = finder.findRequiredView(source, 2131558592, "field 'etSearchMobileLike'");
    target.etSearchMobileLike = finder.castView(view, 2131558592, "field 'etSearchMobileLike'");
    view = finder.findRequiredView(source, 2131558593, "field 'etSearchIdNumberLike'");
    target.etSearchIdNumberLike = finder.castView(view, 2131558593, "field 'etSearchIdNumberLike'");
  }

  @Override public void unbind(T target) {
    target.memberTopBar = null;
    target.memberSearchBar = null;
    target.btnSearchMember = null;
    target.btnQuitSearch = null;
    target.btnAddMember = null;
    target.listview = null;
    target.etSearchNameLike = null;
    target.etSearchMobileLike = null;
    target.etSearchIdNumberLike = null;
  }
}
