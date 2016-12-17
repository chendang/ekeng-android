// Generated code from Butter Knife. Do not modify!
package com.cnnet.otc.health.activities;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AddMemberActivity$$ViewBinder<T extends com.cnnet.otc.health.activities.AddMemberActivity> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131624020, "field 'trWithDoctor'");
    target.trWithDoctor = finder.castView(view, 2131624020, "field 'trWithDoctor'");
    view = finder.findRequiredView(source, 2131624021, "field 'spWithDoctor'");
    target.spWithDoctor = finder.castView(view, 2131624021, "field 'spWithDoctor'");
    view = finder.findRequiredView(source, 2131624018, "field 'ivMemberHead'");
    target.ivMemberHead = finder.castView(view, 2131624018, "field 'ivMemberHead'");
    view = finder.findRequiredView(source, 2131624017, "field 'ivBackBtn'");
    target.ivBackBtn = finder.castView(view, 2131624017, "field 'ivBackBtn'");
    view = finder.findRequiredView(source, 2131624022, "field 'mName'");
    target.mName = finder.castView(view, 2131624022, "field 'mName'");
    view = finder.findRequiredView(source, 2131624023, "field 'mGender' and method 'genderSelected'");
    target.mGender = finder.castView(view, 2131624023, "field 'mGender'");
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
    view = finder.findRequiredView(source, 2131624024, "field 'mTelephone'");
    target.mTelephone = finder.castView(view, 2131624024, "field 'mTelephone'");
    view = finder.findRequiredView(source, 2131624025, "field 'mBirthday' and method 'setDate'");
    target.mBirthday = finder.castView(view, 2131624025, "field 'mBirthday'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.setDate();
        }
      });
    view = finder.findRequiredView(source, 2131624026, "field 'mLandline'");
    target.mLandline = finder.castView(view, 2131624026, "field 'mLandline'");
    view = finder.findRequiredView(source, 2131624027, "field 'mIdnumber'");
    target.mIdnumber = finder.castView(view, 2131624027, "field 'mIdnumber'");
    view = finder.findRequiredView(source, 2131624028, "field 'mSSN'");
    target.mSSN = finder.castView(view, 2131624028, "field 'mSSN'");
    view = finder.findRequiredView(source, 2131624029, "field 'mAddress'");
    target.mAddress = finder.castView(view, 2131624029, "field 'mAddress'");
    view = finder.findRequiredView(source, 2131624030, "field 'mAnamnesis'");
    target.mAnamnesis = finder.castView(view, 2131624030, "field 'mAnamnesis'");
    view = finder.findRequiredView(source, 2131624031, "method 'confirmAddMember'");
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
