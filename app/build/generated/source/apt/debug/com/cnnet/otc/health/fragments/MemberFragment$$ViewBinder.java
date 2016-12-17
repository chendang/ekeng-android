// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class MemberFragment$$ViewBinder<T extends com.cnnet.otc.health.fragments.MemberFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624151, "field 'memberTopBar'");
    target.memberTopBar = finder.castView(view, 2131624151, "field 'memberTopBar'");
    view = finder.findRequiredView(source, 2131624154, "field 'memberSearchBar'");
    target.memberSearchBar = finder.castView(view, 2131624154, "field 'memberSearchBar'");
    view = finder.findRequiredView(source, 2131624153, "field 'btnSearchMember'");
    target.btnSearchMember = finder.castView(view, 2131624153, "field 'btnSearchMember'");
    view = finder.findRequiredView(source, 2131624158, "field 'btnQuitSearch'");
    target.btnQuitSearch = finder.castView(view, 2131624158, "field 'btnQuitSearch'");
    view = finder.findRequiredView(source, 2131624152, "field 'btnAddMember'");
    target.btnAddMember = finder.castView(view, 2131624152, "field 'btnAddMember'");
    view = finder.findRequiredView(source, 2131624043, "field 'listview'");
    target.listview = finder.castView(view, 2131624043, "field 'listview'");
    view = finder.findRequiredView(source, 2131624155, "field 'etSearchNameLike'");
    target.etSearchNameLike = finder.castView(view, 2131624155, "field 'etSearchNameLike'");
    view = finder.findRequiredView(source, 2131624156, "field 'etSearchMobileLike'");
    target.etSearchMobileLike = finder.castView(view, 2131624156, "field 'etSearchMobileLike'");
    view = finder.findRequiredView(source, 2131624157, "field 'etSearchIdNumberLike'");
    target.etSearchIdNumberLike = finder.castView(view, 2131624157, "field 'etSearchIdNumberLike'");
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
