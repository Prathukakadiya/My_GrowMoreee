package DS;
import Service.StockService;
import Service.StockService.*;

// Linked list (DS) to insert & display stocks
public class StocksList {
    class NewStock{
        NewStock next;
        String Symbol;
        String name;
        double prev;
        double today;
        String category;

        public NewStock(String symbol, String name, String category,double prev, double today) {
            Symbol = symbol;
            this.name = name;
            this.prev = prev;
            this.today = today;
            this.category=category;
        }
    }
    NewStock first=null;

    public void InsertStocks(String symbol, String name,String category, double prev, double today){
        NewStock stock=new NewStock(symbol, name, category, prev, today);
        if(first==null){
            first=stock;
        }
        else{
            NewStock temp=first;
            while(temp.next!=null){
                temp=temp.next;
            }
            temp.next=stock;
        }
    }
    public void DisplayStock(){
        NewStock temp=first;
        System.out.println("-------------------------------------------------------------------------------------------------------");
        System.out.println("SYMBOL         COMPANY                      CATEGORY                     PREV_CLOSE   TODAY_OPEN");
        System.out.println("-------------------------------------------------------------------------------------------------------");
        while(temp.next!=null){
            String sym= StockService.TableShow(temp.Symbol,12);
            String sname=StockService.TableShow(temp.name,33);
            String scategory=StockService.TableShow(temp.category,30);
            double sprev= temp.prev;
            double stoday = temp.today;

            System.out.println(sym + sname + scategory + sprev + "     " + stoday);
            temp=temp.next;
        }
    }
}

