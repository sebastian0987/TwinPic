package com.durrutia.twinpic.domain;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.lang.reflect.Field;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Created by durrutia on 20-Oct-16.
 */
@EqualsAndHashCode(of = "id", callSuper = false)
public class BaseModel {

    @Getter
    Long id;

    /**
     * json style
     */
    private static final ToStringStyle jsonToStringStyle = new JsonToStringStyle();

    /**
     * @see Object#toString()
     */
    @Override
    public String toString() {

        // FIXME: Deep serialization
        // return ModelConverter.toJson(this);

        final ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this, jsonToStringStyle) {

            /**
             * Returns whether or not to append the given <code>Field</code>.
             * <ul>
             * <li>Transient fields are appended only if {@link #isAppendTransients()} returns <code>true</code>.
             * <li>Static fields are appended only if {@link #isAppendStatics()} returns <code>true</code>.
             * <li>Inner class fields are not appended.</li>
             * </ul>
             *
             * @param field The Field to test.
             * @return Whether or not to append the given <code>Field</code>.
             */
            @Override
            protected boolean accept(Field field) {

                if (!super.accept(field)) {
                    return false;
                }

                final String name = field.getName();

                if (name.startsWith("_")) {
                    return false;
                }

                if (name.startsWith("when")) {
                    return false;
                }

                if (name.startsWith("log")) {
                    return false;
                }

                return true;
            }

        };

        return builder.toString();
    }

    /**
     * <p>
     * <code>ToStringStyle</code> that outputs with JSON format.
     * </p>
     * <p>
     * <p>
     * This is an inner class rather than using
     * <code>StandardToStringStyle</code> to ensure its immutability.
     * </p>
     */
    private static final class JsonToStringStyle extends ToStringStyle {

        private static final long serialVersionUID = 1L;

        /**
         * The summary size text start <code>'&gt;'</code>.
         */
        private String FIELD_NAME_PREFIX = "\"";

        /**
         * Indenting of inner lines.
         */
        private int indent = 2;

        /**
         * Current indenting.
         */
        private int spaces = 2;


        /**
         * <p>
         * Constructor.
         * </p>
         * <p>
         * <p>
         * Use the static constant rather than instantiating.
         * </p>
         */
        private JsonToStringStyle() {
            super();

            resetIndent();

            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);

            this.setFieldNameValueSeparator(" : ");

            this.setNullText("null");

            this.setSummaryObjectStartText("\"<");
            this.setSummaryObjectEndText(">\"");

            this.setSizeStartText("\"<size=");
            this.setSizeEndText(">\"");
        }

        /**
         * Resets the fields responsible for the line breaks and indenting.
         * Must be invoked after changing the {@link #spaces} value.
         */
        private void resetIndent() {

            this.setArrayStart("[" + SystemUtils.LINE_SEPARATOR + spacer(spaces));
            this.setArrayEnd(SystemUtils.LINE_SEPARATOR + spacer(spaces - indent) + "]");

            this.setArraySeparator("," + SystemUtils.LINE_SEPARATOR + spacer(spaces));

            this.setFieldSeparator("," + SystemUtils.LINE_SEPARATOR + spacer(spaces));

            this.setContentStart("{" + SystemUtils.LINE_SEPARATOR + spacer(spaces));
            this.setContentEnd(SystemUtils.LINE_SEPARATOR + spacer(spaces - indent) + "}");

        }

        /**
         * Creates a StringBuilder responsible for the indenting.
         *
         * @param spaces how far to indent
         * @return a StringBuilder with {spaces} leading space characters.
         */
        private StringBuilder spacer(int spaces) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < spaces; i++) {
                sb.append(" ");
            }
            return sb;
        }

        /**
         *
         * @param buffer
         * @param fieldName
         * @param value
         */
        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {

            spaces += indent;
            resetIndent();

            if (value instanceof BaseModel) {
                super.appendDetail(buffer, fieldName, ((BaseModel) value).getId());
            } else {
                super.appendDetail(buffer, fieldName, value);
            }

            spaces -= indent;
            resetIndent();

        }

        /**
         *
         * @param buffer
         * @param fieldName
         */
        @Override
        protected void appendFieldStart(StringBuffer buffer, String fieldName) {

            if (fieldName == null) {
                throw new UnsupportedOperationException(
                        "Field names are mandatory when using JsonToStringStyle");
            }

            super.appendFieldStart(buffer, FIELD_NAME_PREFIX + fieldName + FIELD_NAME_PREFIX);
        }

    }

}
