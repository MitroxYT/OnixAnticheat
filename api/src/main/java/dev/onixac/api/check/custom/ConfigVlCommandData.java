package dev.onixac.api.check.custom;

public class ConfigVlCommandData {
  private int vl;
  private int alertInterval;
  private String command;
  public ConfigVlCommandData(int vl,int alertInterval,String command) {
      this.vl = vl;
      this.alertInterval = alertInterval;
      this.command = command;
  }

    public int getAlertInterval() {
        return alertInterval;
    }

    public String getCommand() {
        return command;
    }

    public int getVl() {
      return vl;
    }

    public void setVl(int vl) {
        this.vl = vl;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setAlertInterval(int alertInterval) {
        this.alertInterval = alertInterval;
    }
}
