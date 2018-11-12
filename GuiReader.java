package labactivityDAD;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.JButton;
import javax.swing.JTextArea;
import java.awt.Color;

public class GuiReader {

	private JFrame frame;
	private JTextField textField;
	public JSONArray jsnArr;

	public JSONArray getJsnArr() {
		return jsnArr;
	}

	public void setJsnArr(JSONArray jsnArr) {
		this.jsnArr = jsnArr;
	}
	
	public JSONObject makeHttpRequest(String url,String method,List<NameValuePair>params) {
		InputStream is = null;
		String json = "";
		JSONObject jObj = null;
		
		try {
			if(method == "POST") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}else if(method == "GET") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine())!=null) {
				sb.append(line +  "\n");
			}
			is.close();
			json = sb.toString();
			jObj = new JSONObject(json);
		}catch (JSONException e) {
			try {
				JSONArray jsnArr = new JSONArray(json);
				jObj = jsnArr.getJSONObject(0);
				setJsnArr(jsnArr);
			}catch(JSONException ee) {
				ee.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
		
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiReader window = new GuiReader();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiReader() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 783, 487);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblSearchFilm = new JLabel("SEARCH FILM STAR DETAILS");
		lblSearchFilm.setBounds(246, 13, 237, 16);
		frame.getContentPane().add(lblSearchFilm);
		
		JLabel lblContent = new JLabel("Gender : ");
		lblContent.setBounds(52, 75, 118, 16);
		frame.getContentPane().add(lblContent);
		
		textField = new JTextField();
		textField.setBounds(182, 72, 331, 22);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBackground(Color.PINK);
		textArea_1.setBounds(52, 136, 565, 276);
		frame.getContentPane().add(textArea_1);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread thread1=new Thread(new Runnable()
				{

					@Override
					public void run() {
						// TODO Auto-generated method stub
						List<NameValuePair> params=new ArrayList<NameValuePair> ();
						params.add(new BasicNameValuePair("gender", textField.getText()));
				
						String strurl = "https://ghibliapi.herokuapp.com/people";
						JSONObject jobj=makeHttpRequest(strurl,"GET",params);
						jsnArr=getJsnArr();
						System.out.println(textField.getText());
						try {
							if(jsnArr.length()>0)
							{
								StringBuilder sb=new StringBuilder();
								for(int i=0;i<jsnArr.length();i++)
								{
									JSONObject result=jsnArr.getJSONObject(i);
									String Name=result.optString("name");
									String Age=result.optString("age");
									String Films=result.optString("films");
									
									String strSetText = "Name :"+Name +" | Age :"+Age+" | Films:"+Films;
								
									sb.append(strSetText+" \n");
									
								}
								
								textArea_1.setText(sb.toString());
							}
							
						} catch (Exception e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}
						
					}
			
				});thread1.start();
				
			}
		});
		btnSearch.setBounds(527, 71, 97, 25);
		frame.getContentPane().add(btnSearch);
		
	}
}
