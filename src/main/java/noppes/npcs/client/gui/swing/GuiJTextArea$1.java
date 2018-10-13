package noppes.npcs.client.gui.swing;

class GuiJTextArea$1 implements Runnable {
   // $FF: synthetic field
   final GuiJTextArea this$0;

   GuiJTextArea$1(GuiJTextArea this$0) {
      this.this$0 = this$0;
   }

   public void run() {
      this.this$0.listener.saveText(GuiJTextArea.access$000(this.this$0).getText());
   }
}
