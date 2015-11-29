package siamraf;

public class CatImage {
    private final String imageLocation;
    private final String imageName;
    private final byte[] imageData;

    public CatImage(String imageLocation, String imageName, byte[] imageData) {
        this.imageLocation = imageLocation;
        this.imageName = imageName;
        this.imageData = imageData;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public String getImageName() {
        return imageName;
    }

    public byte[] getImageData() {
        return imageData;
    }
}
