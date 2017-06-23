package album;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;

public class Album implements java.io.Serializable {

	String title;
	int id;
	String artist;
	String year;

	public Album(String title, String artist, String year, int id) {
		this.title = title;
		this.artist = artist;
		this.year = year;
		this.id = id;
	}

	public static void write(ArrayList<Album> albums) {
		try {
			FileOutputStream fileOut = new FileOutputStream("album.data");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(albums);
			out.close();
			fileOut.close();
			System.out.println("Serialized data is saved in hospital.data");
		} catch (IOException ex) {
			System.out.println(ex);
		}
	}

	public static void main(String[] args) {
		AlbumID id = new AlbumID();

		port(3000);

		ArrayList<Album> albums = new ArrayList<Album>();

		get("/album/create/:title/:artist/:year", (req, res) -> {
			System.out.println("request made");
			Album newAlbum = new Album(req.params(":title"), req.params(":artist"), req.params(":year"), id.setID());
			albums.add(newAlbum);
			write(albums);
			return "Added album: " + req.params(":title") + " to list!  ID = " + newAlbum.id;
		});

		get("/album/lookup/:id", (req, res) -> {
			System.out.println("request made");
			int getid = Integer.parseInt(req.params(":id"));
			for (int i = 0; i < albums.size(); i++) {
				if (albums.get(i).id == getid) {
					return "ID " + getid + " is " + albums.get(i).title + albums.get(i).artist + albums.get(i).year;
				}
			}
			return "No album found with that ID.";
		});

		get("/album/remove/:id", (req, res) -> {
			System.out.println("request made");
			int getid = Integer.parseInt(req.params(":id"));
			for (int i = 0; i < albums.size(); i++) {
				if (albums.get(i).id == getid) {
					albums.remove(albums.get(i));
					write(albums);
					return "ID " + getid + " was removed.";
				}
			}
			return "No album found with that ID.";
		});

		get("/", (req, res) -> {
			// Use JTWIG to generate XML

			JtwigTemplate template = JtwigTemplate.classpathTemplate("template.twig");
			JtwigModel model = JtwigModel.newModel().with("albums", albums);

			return template.render(model);

			// Below is how you can push HTML out WITHOUT JTWIG
			// String h1 = "<h1>Album Collection</h1>";
			// String body = "";
			// for (int i = 0; i < albums.size(); i++) {
			// body = body + "<div> Album: " + albums.get(i).title + " Artist: "
			// + albums.get(i).artist
			// + " Year: " + albums.get(i).year + "</div>";
			// }
			// String html = "<html><head><body>" + h1 + body +
			// "</body></head></html>";
			// return html;
		});

		get("/json", (req, res) -> {
			return "{\"title\": \"Gone with the Wind\", year: \"1939\"}";
		});

		get("/gson", (req, res) -> {
			Gson gson = new Gson();
			String gToJson;
			gToJson = gson.toJson(albums);
			return gToJson;
			// parse and use "DOM"
			
		});
		
		
	}

}

class AlbumID {
	int id = 0;

	public int setID() {
		id++;
		return id;
	}

}
