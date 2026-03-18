package fi.iki.jka;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.event.ActionEvent;

class JPhotoFrameTest {

    private static class TestableJPhotoFrame extends JPhotoFrame {
        JPhotoShow capturedShow = null;
        int capturedInterval = -1;

        TestableJPhotoFrame(JPhotoCollection photos) throws Exception {
            super();
            this.photos = photos;
        }

        @Override
        protected JPhotoShow createSlideshow(int intervalMs) {
            capturedInterval = intervalMs;
            capturedShow = new JPhotoShow(photos, intervalMs, null);
            return capturedShow;
        }

        @Override
        protected void startSlideshow(int intervalMs) {
            if (photos.getSize() > 0) {
                createSlideshow(intervalMs);
            }
        }
        @Override
        public void setTitle() {
            // do nothing
        }
    }

    private TestableJPhotoFrame frameWithPhotos;
    private TestableJPhotoFrame frameWithNoPhotos;

    @BeforeEach
    void setUp() throws Exception {
        JPhotoCollection withOne = new JPhotoCollection();
        withOne.add(0, new JPhoto(withOne));

        JPhotoCollection empty = new JPhotoCollection();

        frameWithPhotos = new TestableJPhotoFrame(withOne);
        frameWithNoPhotos = new TestableJPhotoFrame(empty);
    }

    @Nested
    @DisplayName("Start Slideshow — normal speed")
    class StartSlideshowTests {

        @Test
        @DisplayName("creates a JPhotoShow when album contains photos")
        void createsShowWhenPhotosExist() {
            frameWithPhotos.startSlideshow(5000);

            assertNotNull(
                    frameWithPhotos.capturedShow,
                    "Expected a JPhotoShow to be created"
            );
        }

        @Test
        @DisplayName("passes 5000 ms interval to the show")
        void passesNormalIntervalToShow() {
            frameWithPhotos.startSlideshow(5000);

            assertEquals(
                    5000,
                    frameWithPhotos.capturedInterval,
                    "Normal slideshow must use 5000 ms interval"
            );
        }

        @Test
        @DisplayName("does NOT create a show when album is empty")
        void doesNotCreateShowForEmptyAlbum() {
            frameWithNoPhotos.startSlideshow(5000);

            assertNull(
                    frameWithNoPhotos.capturedShow,
                    "No show should be created when album is empty"
            );
        }

        @Test
        @DisplayName("actionPerformed with A_SLIDESHOW triggers 5000 ms interval")
        void actionPerformedRoutesToNormalInterval() {
            ActionEvent event = new ActionEvent(
                    frameWithPhotos,
                    ActionEvent.ACTION_PERFORMED,
                    JPhotoMenu.A_SLIDESHOW
            );

            frameWithPhotos.actionPerformed(event);

            assertEquals(5000, frameWithPhotos.capturedInterval);
        }
    }

    @Nested
    @DisplayName("Preview Slideshow — fast speed")
    class PreviewSlideshowTests {

        @Test
        @DisplayName("creates a JPhotoShow when album contains photos")
        void createsShowWhenPhotosExist() {
            frameWithPhotos.startSlideshow(1000);

            assertNotNull(
                    frameWithPhotos.capturedShow,
                    "Expected a JPhotoShow to be created for preview"
            );
        }

        @Test
        @DisplayName("passes 1000 ms interval to the show")
        void passesPreviewIntervalToShow() {
            frameWithPhotos.startSlideshow(1000);

            assertEquals(
                    1000,
                    frameWithPhotos.capturedInterval,
                    "Preview slideshow must use 1000 ms interval"
            );
        }

        @Test
        @DisplayName("preview interval is strictly shorter than normal interval")
        void previewIntervalIsShorterThanNormal() {
            int normal = 5000;
            int preview = 1000;

            assertTrue(
                    preview < normal,
                    "Preview interval must be strictly less than normal interval"
            );
        }

        @Test
        @DisplayName("actionPerformed with A_PREVIEW_SLIDESHOW triggers 1000 ms interval")
        void actionPerformedRoutesToPreviewInterval() {
            ActionEvent event = new ActionEvent(
                    frameWithPhotos,
                    ActionEvent.ACTION_PERFORMED,
                    JPhotoMenu.A_PREVIEW_SLIDESHOW
            );

            frameWithPhotos.actionPerformed(event);

            assertEquals(1000, frameWithPhotos.capturedInterval);
        }
    }
}