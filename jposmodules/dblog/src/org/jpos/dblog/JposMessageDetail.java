package org.jpos.dblog;


import java.io.*;

 /**
  * JposMessageDetail Value Object.
  * This class is value object representing database table jposMessageDetail
  * This class is intented to be used together with associated Dao object.
  */

 /**
  * jposMessageDetail column handling code.
  * This sourcecode has been generated by FREE DaoGen generator version 2.2.2.
  * The usage of generated code is restricted to OpenSource software projects
  * only. DaoGen is available in http://titaniclinux.net/daogen/
  * It has been programmed by Tuomo Lukka, Tuomo.Lukka@iki.fi
  *
  * DaoGen license: The following DaoGen generated source code is licensed
  * under the terms of GNU GPL license. The full text for license is available
  * in GNU project's pages: http://www.gnu.org/copyleft/gpl.html
  *
  * If you wish to use the DaoGen generator to produce code for closed-source
  * commercial applications, you must pay the lisence fee. The price is
  * 5 USD or 5 Eur for each database table, you are generating code for.
  * (That includes unlimited amount of iterations with all supported languages
  * for each database table you are paying for.) Send mail to
  * "Tuomo.Lukka@iki.fi" for more information. Thank you!
  */



public class JposMessageDetail implements Cloneable, Serializable {

    /**
     * Persistent Instance variables. This data is directly
     * mapped to the columns of database table.
     */
    private int detailId;
    private int msgId;
    private String bit;
    private String value;
    private String msgType;



    /**
     * Constructors. DaoGen generates two constructors by default.
     * The first one takes no arguments and provides the most simple
     * way to create object instance. The another one takes one
     * argument, which is the primary key of the corresponding table.
     */

    public JposMessageDetail () {

    }

    public JposMessageDetail (int detailIdIn) {

          this.detailId = detailIdIn;

    }


    /**
     * Get- and Set-methods for persistent variables. The default
     * behaviour does not make any checks against malformed data,
     * so these might require some manual additions.
     */

    public int getDetailId() {
          return this.detailId;
    }
    public void setDetailId(int detailIdIn) {
          this.detailId = detailIdIn;
    }

    public int getMsgId() {
          return this.msgId;
    }
    public void setMsgId(int msgIdIn) {
          this.msgId = msgIdIn;
    }

    public String getBit() {
          return this.bit;
    }
    public void setBit(String bitIn) {
          this.bit = bitIn;
    }

    public String getValue() {
          return this.value;
    }
    public void setValue(String valueIn) {
          this.value = valueIn;
    }

    public String getMsgType() {
          return this.msgType;
    }
    public void setMsgType(String msgTypeIn) {
          this.msgType = msgTypeIn;
    }



    /**
     * setAll allows to set all persistent variables in one method call.
     * This is useful, when all data is available and it is needed to
     * set the initial state of this object. Note that this method will
     * directly modify instance variales, without going trough the
     * individual set-methods.
     */

    public void setAll(int detailIdIn,
          int msgIdIn,
          String bitIn,
          String valueIn,
          String msgTypeIn) {
          this.detailId = detailIdIn;
          this.msgId = msgIdIn;
          this.bit = bitIn;
          this.value = valueIn;
          this.msgType = msgTypeIn;
    }


    /**
     * hasEqualMapping-method will compare two JposMessageDetail instances
     * and return true if they contain same values in all persistent instance
     * variables. If hasEqualMapping returns true, it does not mean the objects
     * are the same instance. However it does mean that in that moment, they
     * are mapped to the same row in database.
     */
    public boolean hasEqualMapping(JposMessageDetail valueObject) {

          if (valueObject.getDetailId() != this.detailId) {
                    return(false);
          }
          if (valueObject.getMsgId() != this.msgId) {
                    return(false);
          }
          if (this.bit == null) {
                    if (valueObject.getBit() != null)
                           return(false);
          } else if (!this.bit.equals(valueObject.getBit())) {
                    return(false);
          }
          if (this.value == null) {
                    if (valueObject.getValue() != null)
                           return(false);
          } else if (!this.value.equals(valueObject.getValue())) {
                    return(false);
          }
          if (this.msgType == null) {
                    if (valueObject.getMsgType() != null)
                           return(false);
          } else if (!this.msgType.equals(valueObject.getMsgType())) {
                    return(false);
          }

          return true;
    }



    /**
     * toString will return String object representing the state of this
     * valueObject. This is useful during application development, and
     * possibly when application is writing object states in textlog.
     */
    public String toString() {
        StringBuffer out = new StringBuffer(this.getDaogenVersion());
        out.append("\nclass JposMessageDetail, mapping to table jposMessageDetail\n");
        out.append("Persistent attributes: \n");
        out.append("detailId = " + this.detailId + "\n");
        out.append("msgId = " + this.msgId + "\n");
        out.append("bit = " + this.bit + "\n");
        out.append("value = " + this.value + "\n");
        out.append("msgType = " + this.msgType + "\n");
        return out.toString();
    }


    /**
     * Clone will return identical deep copy of this valueObject.
     * Note, that this method is different than the clone() which
     * is defined in java.lang.Object. Here, the retuned cloned object
     * will also have all its attributes cloned.
     */
    public Object clone() {
        JposMessageDetail cloned = new JposMessageDetail();

        cloned.setDetailId(this.detailId);
        cloned.setMsgId(this.msgId);
        cloned.setBit(new String(this.bit));
        cloned.setValue(new String(this.value));
        cloned.setMsgType(new String(this.msgType));


        return cloned;
    }



    /**
     * getDaogenVersion will return information about
     * generator which created these sources.
     */
    public String getDaogenVersion() {
        return "DaoGen version 2.2.2";
    }

}


