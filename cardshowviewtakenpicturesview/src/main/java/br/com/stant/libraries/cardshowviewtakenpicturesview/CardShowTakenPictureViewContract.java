package br.com.stant.libraries.cardshowviewtakenpicturesview;

import android.content.Intent;
import android.view.View;

import java.util.List;

import br.com.stant.libraries.cardshowviewtakenpicturesview.domain.enums.CardShowTakenPictureStateEnum;

/**
 * Created by denisvieira on 08/06/17.
 */

public interface CardShowTakenPictureViewContract {

    void pickPictureToFinishServiceInspectionFormFilled(View view);
    void dispatchTakePictureOrPickGalleryIntent();
    void addImageOnActivityResult(int requestCode, int resultCode, Intent data);
    void checkIfHasImages();
    void showPreviewPicDialog(String imageUrl);
    void closePreviewPicDialog(View View);
    void showEditStateViewConfiguration(View view);
    void saveImageStateViewConfiguration(View view);
    void cancelEditImagesStateViewConfiguration(View view);
    void blockEditStateViewConfiguration();
    void unblockEditStateViewConfiguration();
    void setCardStateEnum(CardShowTakenPictureStateEnum cardStateEnum);

    interface OnSavedCardListener {
        void onSaved(List<String> imagesAsAdded, List<String> imagesAsRemoved);
        void onCancel();
    }

    void setOnSavedCardListener(OnSavedCardListener mOnSavedCardListener);

}
