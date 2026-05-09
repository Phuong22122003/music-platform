import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PinturaEditorOptions } from '@pqina/pintura';
import { openEditor } from '../../utils/image-edit';

@Component({
  selector: 'app-dynamic-form',
  standalone: false,
  templateUrl: './dynamic-form.component.html',
  styleUrl: './dynamic-form.component.scss',
  encapsulation: ViewEncapsulation.None,
})
export class DynamicFormComponent implements OnInit {
  @Input() formFields: {
    name: string;
    label: string;
    type: string;
    columnSpan?: number;
    placeholder?: string;
    validators?: any[];
    options?: {
      imageOrientation?: any;
      widthImage?: number;
      heighImage?: number;
      defaultValue?: any;
      includeDefault?: boolean;
    };
    dataSelect?: any;
  }[] = [];
  @Input() key!: string;
  @Input() disabled: boolean = false;
  @Output() submit = new EventEmitter<{ [key: string]: any }>();
  @Output() image = new EventEmitter<File>();
  form!: FormGroup;
  result: { image?: File } = {};
  selectedImageFile: File | null = null;

  // Image
  editorOptions = {
    imageCropMinSize: { width: 400, height: 400 },
    cropEnableImageSelection: false,
    imageCropAspectRatio: 1,
  } as PinturaEditorOptions;
  editedImageUrl: string = '';
  hasImageHorizontal: boolean = true;

  constructor(private fb: FormBuilder) {
    this.formFields.forEach;
  }

  normalizeValue(value: any): string {
    if (value === null || value === undefined) return '';
    return value.toString().toLowerCase();
  }

  compareValues(value1: any, value2: any): boolean {
    if (value1 === null || value1 === undefined) value1 = '';
    if (value2 === null || value2 === undefined) value2 = '';
    return this.normalizeValue(value1) === this.normalizeValue(value2);
  }

  ngOnInit(): void {
    console.log(this.formFields);
    const controls: { [key: string]: any } = {};
    this.formFields.forEach((field) => {
      if (field.type === 'image') {
        this.editorOptions = {
          ...this.editorOptions,
          imageCropMinSize: {
            width: (field.options?.widthImage as number) || 400,
            height: (field.options?.heighImage as number) || 400,
          },
        };

        if (field.options?.imageOrientation !== 'horizontal') {
          this.hasImageHorizontal = false;
        }
        if (field.options?.defaultValue) {
          this.selectedImageFile = field.options.defaultValue as File;
          this.editedImageUrl = URL.createObjectURL(this.selectedImageFile);
        }
        console.log(this.hasImageHorizontal);
        return;
      }
      let value =
        field.options?.defaultValue ??
        (field.type === 'multi-select' ? [] : null);

      // For radio buttons, normalize the default value
      if (field.type === 'radio' && value) {
        value = this.normalizeValue(value);
      }

      controls[field.name] = [value, field.validators || []];
    });
    this.form = this.fb.group(controls);

    // Add value change subscriptions for radio buttons
    this.formFields.forEach((field) => {
      if (field.type === 'radio') {
        const control = this.form.get(field.name);
        if (control) {
          control.valueChanges.subscribe((value) => {
            // Normalize the value when it changes
            const normalizedValue = this.normalizeValue(value);
            if (normalizedValue !== value) {
              control.setValue(normalizedValue, { emitEvent: false });
            }
          });
        }
      }
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      Object.keys(this.form.controls).forEach((field) => {
        this.form.get(field)?.markAsTouched();
      });
      return;
    }

    this.result = { ...this.form.value };
    this.submit.emit(this.result);
  }
  trackByIndex(index: number, field: any): number {
    return field.name;
  }

  onEditImage(event: Event) {
    event.preventDefault();
    openEditor(
      this.selectedImageFile as File,
      this.editorOptions,
      (editedFile, editedImageUrl) => {
        this.selectedImageFile = editedFile;
        this.editedImageUrl = editedImageUrl;
        this.image.emit(this.selectedImageFile);
      }
    );
  }

  onDeleteImage(event: Event) {
    event.preventDefault();
    this.selectedImageFile = null;
    this.editedImageUrl = '';
  }

  onSelectedImageFile(event: Event) {
    const inputFile = event.target as HTMLInputElement;
    if (inputFile.files && inputFile.files.length > 0) {
      this.selectedImageFile = inputFile.files[0];
      openEditor(
        this.selectedImageFile,
        this.editorOptions,
        (editedFile, editedImageUrl) => {
          this.selectedImageFile = editedFile;
          this.editedImageUrl = editedImageUrl;
          this.image.emit(this.selectedImageFile);
        }
      );
    }
  }

  onInputChange(event: Event, fieldName: string) {
    const inputElement = event.target as HTMLInputElement;
    const formControl = this.form.get(fieldName);

    if (!formControl) return;

    const defaultValue =
      this.formFields.find((f) => f.name === fieldName)?.options
        ?.defaultValue || '';

    // Nếu giá trị mới không bắt đầu bằng defaultValue -> reset lại
    if (!inputElement.value.startsWith(defaultValue as string)) {
      formControl.setValue(defaultValue);
    }
  }
}
