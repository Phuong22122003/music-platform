function copyLink(url: string): void {
  navigator.clipboard.writeText(url).then(
    () => {
      console.log('Copied to clipboard:', url);
    },
    (err) => {
      console.error('Failed to copy: ', err);
    }
  );
}
export default copyLink;
