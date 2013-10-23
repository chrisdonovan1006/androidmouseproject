package itt.t00154755.mathstest;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


public class GridImageAdapter extends BaseAdapter
{
    private Context context;
    
    // Keep all Images in array
    public Integer[] numberIds = {
            R.drawable.pic_1, 
            R.drawable.pic_2,
            R.drawable.pic_3, 
            R.drawable.pic_2, 
            R.drawable.pic_3,
            R.drawable.pic_1, 
            R.drawable.pic_3, 
            R.drawable.pic_1,
            R.drawable.pic_2, 
            R.drawable.pic_1, 
            R.drawable.pic_2,
            R.drawable.pic_3, 
            R.drawable.pic_2, 
            R.drawable.pic_3,
            R.drawable.pic_1, 
            R.drawable.pic_3, 
            R.drawable.pic_1,
            R.drawable.pic_2, 

    };
 
    // Constructor
    public GridImageAdapter(Context c){
        context = c;
    }
 
    @Override
    public int getCount() {
        return numberIds.length;
    }
 
    @Override
    public Object getItem(int position) {
        return numberIds[position];
    }
 
    @Override
    public long getItemId(int position) {
        return 0;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(numberIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(70, 70));
        return imageView;
    }

}
