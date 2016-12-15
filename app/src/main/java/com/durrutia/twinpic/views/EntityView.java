package com.durrutia.twinpic.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import butterknife.ButterKnife;

/**
 * @author Diego P. Urrutia Astorga
 * @version 201511031918
 */
public abstract class EntityView<Entity> extends RelativeLayout {

    /**
     * @param context
     */
    public EntityView(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public EntityView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public EntityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public EntityView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Setup de los hijos
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    /**
     * @param entity
     */
    public abstract void setEntity(final Entity entity);

    /**
     * @param parent
     * @return the EntityView
     */
    public static EntityView inflate(final ViewGroup parent, final int layoutId) {
        return (EntityView) LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }

}
