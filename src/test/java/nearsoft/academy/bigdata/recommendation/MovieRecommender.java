package nearsoft.academy.bigdata.recommendation;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


public class MovieRecommender {
    
    
    private int totalReviews = 0;
    private int totalUsers = 0;
    private int totalProducts = 0;
    
    private int ProductNumb=0;
    private int UserNumb=0;
    
    private final HashMap<String, Integer> usersHash = new HashMap();
    private final HashMap<String, Integer> productsHash = new HashMap();
    private final HashMap<Integer, String> InverseProductsHash = new HashMap();
    
    
    // Constructor
    public MovieRecommender(String path) throws IOException, TasteException {
       File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));
        File reviews = new File("RMovies.csv");
        FileWriter fw = new FileWriter(reviews);
        BufferedWriter wr = new BufferedWriter(fw);
        
// Ocupamos le hashmap

        String userId = "", productId = "", score;
         String line;
        while ((line = br.readLine()) != null) {
          switch (line.split(" ")[0]) {
              case "product/productId:":
                  productId = line.split(" ")[1];
                  if (!productsHash.containsKey(productId)) {
                    totalProducts++;
                    productsHash.put(productId, totalProducts);
                    InverseProductsHash.put(totalProducts, productId);
                    ProductNumb = totalProducts;
                  }
                  else{
                      ProductNumb = productsHash.get(productId); 
                  }
                  break;
              case "review/userId:":
                  userId = line.split(" ")[1];
                  if (!usersHash.containsKey(userId)) {
                    totalUsers++;
                    usersHash.put(userId,totalUsers);
                    UserNumb = totalUsers;
                  }
                  else{
                      UserNumb = usersHash.get(userId); 
                  }
                  break;
              case "review/score:":
                  score = line.split(" ")[1];
                  wr.write(UserNumb+","+ProductNumb+","+score+"\n");
                  totalReviews++;
                  break;
          }
        }
        br.close();
        wr.close();
        
    }
    
    public int getTotalReviews() {
        return totalReviews;
    }
    
    public int getTotalProducts() {
        return totalProducts;
    }
    
    public int getTotalUsers() {
        return totalUsers;
    }
    
    public List<String> getRecommendationsForUser(String userId) throws TasteException, IOException  {
        DataModel model = new FileDataModel(new File("RMovies.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        
        List <RecommendedItem> recommendations = recommender.recommend(usersHash.get(userId), 3);
        
        List<String> Response = new ArrayList <String>();
        
        for (RecommendedItem recommendation : recommendations) {
            Response.add(InverseProductsHash.get((int)recommendation.getItemID()));
        }
        
        return Response;
    }
    
}