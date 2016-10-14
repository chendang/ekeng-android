// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AddMemberActivity$$ViewBinder<T extends com.cnnet.otc.health.activities.AddMemberActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131558484, "field 'trWithDoctor'");
    target.trWithDoctor = finder.castView(view, 2131558484, "field 'trWithDoctor'");
    view = finder.findRequiredView(source, 2131558485, "field 'spWithDoctor'");
    target.spWithDoctor = finder.castView(view, 2131558485, "field 'spWithDoctor'");
    view = finder.findRequiredView(source, 2131558482, "field 'ivMemberHead'");
    target.ivMemberHead = finder.castView(view, 2131558482, "field 'ivMemberHead'");
    view = finder.findRequiredView(source, 2131558481, "field 'ivBackBtn'");
    target.ivBackBtn = finder.castView(view, 2131558481, "field 'ivBackBtn'");
    view = finder.findRequiredView(source, 2131558486, "field 'mName'");
    target.mName = finder.castView(view, 2131558486, "field 'mName'");
    view = finder.findRequiredView(source, 2131558487, "field 'mGender' and method 'genderSelected'");
    target.mGender = finder.castView(view, 2131558487, "field 'mGender'");
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.genderSelected(p2);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131558488, "field 'mTelephone'");
    target.mTelephone = finder.castView(view, 2131558488, "field 'mTelephone'");
    view = finder.findRequiredView(source, 2131558489, "field 'mBirthday' and method 'setDate'");
    target.mBirthday = finder.castView(view, 2131558489, "field 'mBirthday'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.setDate();
        }
      });
    view = finder.findRequiredView(source, 2131558490, "field 'mLandline'");
    target.mLandline = finder.castView(view, 2131558490, "field 'mLandline'");
    view = finder.findRequiredView(source, 2131558491, "field 'mIdnumber'");
    target.mIdnumber = finder.castView(view, 2131558491, "field 'mIdnumber'");
    view = finder.findRequiredView(source, 2131558492, "field 'mSSN'");
    target.mSSN = finder.castView(view, 2131558492, "field 'mSSN'");
    view = finder.findRequiredView(source, 2131558493, "field 'mAddress'");
    target.mAddress = finder.castView(view, 2131558493, "field 'mAddress'");
    view = finder.findRequiredView(source, 2131558494, "field 'mAnamnesis'");
    target.mAnamnesis = finder.castView(view, 2131558494, "field 'mAnamnesis'");
    view = finder.findRequiredView(source, 2131558495, "method 'confirmAddMember'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.confirmAddMember();
        }
      });
  }

  @Override public void unbind(T target) {
    target.trWithDoctor = null;
    target.spWithDoctor = null;
    target.ivMemberHead = null;
    target.ivBackBtn = null;
    target.mName = null;
    target.mGender = null;
    target.mTelephone = null;
    target.mBirthday = null;
    target.mLandline = null;
    target.mIdnumber = null;
    target.mSSN = null;
    target.mAddress = null;
    target.mAnamnesis = null;
  }
}
