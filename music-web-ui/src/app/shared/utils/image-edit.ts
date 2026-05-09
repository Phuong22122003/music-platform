import {
  openDefaultEditor,
  PinturaEditorModal,
  PinturaEditorOptions,
} from '@pqina/pintura';

export function openEditor(
  fileToEdit: File,
  options: Partial<PinturaEditorOptions>,
  callback: (editedFile: File, editedImageUrl: string) => void
) {
  const reader = new FileReader();
  reader.onload = () => {
    const imageSrc = reader.result as string;

    const editor: PinturaEditorModal = openDefaultEditor({
      ...options,
      src: imageSrc,
    });

    editor.on('process', (res: any) => {
      if (res.dest) {
        const editedImageUrl = URL.createObjectURL(res.dest);
        const editedFile = res.dest;

        callback(editedFile, editedImageUrl);
      }
      editor.close();
    });
  };
  reader.readAsDataURL(fileToEdit);
}
