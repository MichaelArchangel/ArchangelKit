package studio.archangel.toolkit3.utils.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.TintAwareDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.rey.material.widget.RelativeLayout;

import studio.archangel.toolkit3.AngelApplication;
import studio.archangel.toolkit3.R;
import studio.archangel.toolkit3.fragments.AngelFragment;
import studio.archangel.toolkit3.views.AngelRedDot;
import studio.archangel.toolkit3.views.AngelTabHost;


/**
 * Created by Michael on 2015/4/8.
 */
public class AngelTabHelper {
	AngelTabHost host;
	Activity act;
	String last_selected_tab_tag = null;

	public AngelTabHelper(Activity a, AngelTabHost h) {
		act = a;
		host = h;
		host.getTabWidget().setDividerDrawable(null);
	}

	public void addTab(AngelTabConfig config, Class<? extends AngelFragment> fragment) {
		addTab(config, fragment, null);
	}

	public void addTab(AngelTabConfig config, Class<? extends AngelFragment> fragment, Bundle args) {
		if (!config.isSpecialButton()) {
			TabHost.TabSpec tab = host.newTabSpec(config.getText());
			View layout = act.getLayoutInflater().inflate(config.getTabLayoutId(), null);
			TextView text = (TextView) layout.findViewById(R.id.view_tab_text);
			ImageView icon = (ImageView) layout.findViewById(R.id.view_tab_icon);

			int reddot_id = config.getReddotId();
			if (reddot_id != -1) {
				AngelRedDot red_dot = (AngelRedDot) layout.findViewById(reddot_id);
				if (config.hasReddot()) {
					red_dot.setVisibility(View.VISIBLE);
//					red_dot.setStyle(config.isRedDotWithText() ? AngelMaterialProperties.RedDotStyle.text : AngelMaterialProperties.RedDotStyle.simple);
				} else {
					red_dot.setVisibility(View.GONE);
				}
			}

//			View strip = layout.findViewById(R.id.view_tab_strip);
			StateListDrawable drawable = new StateListDrawable();
			Drawable d = config.getSelectedDrawable();
			d = getBitmapDrawableForApi19(d);
			drawable.addState(new int[]{android.R.attr.state_selected}, d);
			drawable.addState(new int[]{android.R.attr.state_pressed}, d);
			drawable.addState(new int[]{android.R.attr.state_focused}, d);
			d = config.getUnselectedDrawable();
			d = getBitmapDrawableForApi19(d);
			drawable.addState(new int[]{}, d);
			icon.setImageDrawable(drawable);

			text.setText(config.getText());

			tab.setIndicator(layout);
			host.addTab(tab, fragment, args);
//			layout.getLayoutParams().height = config.getTabHeight();
//
			if (last_selected_tab_tag == null) {
				last_selected_tab_tag = config.getText();
			}
		} else {
			TabHost.TabSpec tab = host.newTabSpec(config.getText());
			View back = config.constructSpecialButton();
			tab.setIndicator(back);
			host.addTab(tab, fragment, null, true);
//			back.getLayoutParams().height = config.getTabHeight();
		}

	}

	private Drawable getBitmapDrawableForApi19(Drawable d) {
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT && d instanceof TintAwareDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			d.draw(canvas);
			d = new BitmapDrawable(act.getResources(), bitmap);
		}
		return d;
	}

	public void refreshTabStatus(String new_selected_tab_tag) {
		int count = host.getTabWidget().getTabCount();
		for (int i = 0; i < count; i++) {
			View layout = host.getTabWidget().getChildTabViewAt(i);
			if (layout == null) {
				continue;
			}
			if (host.isSpecialButtonTab(i)) {
				continue;
			}
			RelativeLayout back = (RelativeLayout) layout.findViewById(R.id.view_tab_back);
			TextView text = (TextView) layout.findViewById(R.id.view_tab_text);
			ImageView icon = (ImageView) layout.findViewById(R.id.view_tab_icon);
			AngelRedDot red_dot = (AngelRedDot) layout.findViewById(R.id.view_tab_red_dot);
			View strip = layout.findViewById(R.id.view_tab_strip);
			View divider = layout.findViewById(R.id.view_tab_divider);
			boolean selected = text.getText().toString().equals(new_selected_tab_tag);
//			if (selected) {
//				back.();
//			}
			back.setSelected(selected);
//			text.setSelected(selected);
//			icon.setSelected(selected);
//			red_dot.setSelected(selected);
//			strip.setSelected(selected);
//			divider.setSelected(selected);
		}

	}


	public AngelRedDot getRedDotAt(int index) {
		View tab = host.getTabWidget().getChildTabViewAt(index);
		try {
			return (AngelRedDot) tab.findViewById(R.id.view_tab_red_dot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


}
